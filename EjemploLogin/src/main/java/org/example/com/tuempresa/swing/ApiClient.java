package org.example.com.tuempresa.swing;

import org.example.com.tuempresa.swing.model.Solicitud;
import org.example.com.tuempresa.swing.model.Vacante;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.in;

public class ApiClient {
    private static Integer id_empresa;
    public static Integer getIdEmpresa() {
        return id_empresa;
    }

    public static void setIdEmpresa(Integer id) {
        id_empresa = id;
    }

    public String enviarRespuesta(String username,String password)  {
        BufferedReader reader = null;

        try {
            URL url = new URL("http://localhost:8080/usuario/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            //Tipo de contenido que se enviara a la aplicacion en este caso json
            connection.setRequestProperty("Content-Type","application/json");
            //Indica que se enviara el body de la solicitud
            connection.setDoOutput(true);

            // Crear el JSON con los datos de login(Esto es lo que le voy a pasar a Springboot para que lo pueda deserializar y leer correctamente)
            String json = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";


            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            //Leer respuesta
            int respuesta = connection.getResponseCode();
            if(respuesta == HttpURLConnection.HTTP_OK){

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),StandardCharsets.UTF_8));
                StringBuilder respuestaS = new StringBuilder();
                String inputLine;
                while((inputLine = reader.readLine()) != null){
                    respuestaS.append(inputLine);
                }
                System.out.println("Respuesta del servidor: " + respuestaS);

                JSONObject jsonResponse = new JSONObject(respuestaS.toString());

                JSONObject body = jsonResponse.getJSONObject("body");


                this.id_empresa = body.getInt("id_empresa");
                System.out.println("ID de empresa asignado: " + this.id_empresa);

                return body.getString("mensaje");
            }else {
                return "Error: " + respuesta;
            }

        } catch (IOException e) {
            return "Error al conectar: " + e.getMessage();
        }
    }

    public List<Vacante> obtenerVacantes(){

        List<Vacante> vacantes = new ArrayList<>();
        BufferedReader reader = null;

        if (this.id_empresa == null) {
            throw new IllegalStateException("id_empresa no está definido. Inicia sesión primero.");
        }


        try{
            URL url = new URL("http://localhost:8080/vacante/vacantes/" + this.id_empresa);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type","application/json");

            int respuesta = connection.getResponseCode();
            if(respuesta == HttpURLConnection.HTTP_OK){
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder respuestaS = new StringBuilder();
                String inputLine;

                while((inputLine = reader.readLine())!= null){
                    respuestaS.append(inputLine);
                }

                //Procesamos el JSON

                JSONArray jsonArray = new JSONArray(respuestaS.toString());
                    //Procesar las vacantes
                for(int i = 0; i< jsonArray.length(); i++){
                    JSONObject jsonVacante = jsonArray.getJSONObject(i);
                    Vacante vacante = new Vacante();

                    vacante.setIdVacante(jsonVacante.getInt("id_vacante"));
                    vacante.setNombre(jsonVacante.getString("nombre"));
                    vacante.setDescripcion(jsonVacante.optString("descripcion", "No disponible"));
                    vacante.setFecha(jsonVacante.optString("fecha", "No disponible"));
                    vacante.setSalario(jsonVacante.optDouble("salario",0));
                    vacante.setEstatus(jsonVacante.getString("estatus"));

                    //Procesar las solicitudes
                    JSONArray solicitudesJson = jsonVacante.getJSONArray("solicitudes");
                    List<Solicitud> solicitudes = new ArrayList<>();
                    for(int n = 0; n<solicitudesJson.length(); n++){
                        JSONObject jsonSolicitud = solicitudesJson.getJSONObject(n);
                        Solicitud solicitud = new Solicitud();

                        solicitud.setIdSolicitud(jsonSolicitud.getInt("id_solicitud"));
                        solicitud.setArchivo(jsonSolicitud.optString("archivo","No disponible"));
                        solicitud.setComentario(jsonSolicitud.optString("comentario","No disponible"));
                        solicitud.setEstado((jsonSolicitud.getInt("estado")));

                        solicitudes.add(solicitud);
                    }
                    vacantes.add(vacante);
                }



            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return vacantes;
    }
}
