package org.example.com.tuempresa.swing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.com.tuempresa.swing.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.System.in;

public class ApiClient {
    private static Integer id_empresa;

    public static Integer getIdEmpresa() {
        return id_empresa;
    }

    public static void setIdEmpresa(Integer id) {
        id_empresa = id;
    }

    // LOGIN

    public String enviarRespuesta(String username, String password) {
        BufferedReader reader = null;

        try {
            URL url = new URL("http://localhost:8080/usuario/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            //Tipo de contenido que se enviara a la aplicacion en este caso json
            connection.setRequestProperty("Content-Type", "application/json");
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
            if (respuesta == HttpURLConnection.HTTP_OK) {

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder respuestaS = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    respuestaS.append(inputLine);
                }
                System.out.println("Respuesta del servidor: " + respuestaS);

                JSONObject jsonResponse = new JSONObject(respuestaS.toString());

                JSONObject body = jsonResponse.getJSONObject("body");


                this.id_empresa = body.getInt("id_empresa");
                System.out.println("ID de empresa asignado: " + this.id_empresa);

                return body.getString("mensaje");
            } else {
                return "Error: " + respuesta;
            }

        } catch (IOException e) {
            return "Error al conectar: " + e.getMessage();
        }
    }

    public List<Vacante> obtenerVacantes() {

        List<Vacante> vacantes = new ArrayList<>();
        BufferedReader reader = null;

        if (this.id_empresa == null) {
            throw new IllegalStateException("id_empresa no está definido. Inicia sesión primero.");
        }


        try {
            URL url = new URL("http://localhost:8080/vacante/vacantes/" + this.id_empresa);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int respuesta = connection.getResponseCode();
            if (respuesta == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder respuestaS = new StringBuilder();
                String inputLine;

                while ((inputLine = reader.readLine()) != null) {
                    respuestaS.append(inputLine);
                }

                //Procesamos el JSON

                JSONArray jsonArray = new JSONArray(respuestaS.toString());
                //Procesar las vacantes
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonVacante = jsonArray.getJSONObject(i);
                    Vacante vacante = new Vacante();

                    vacante.setIdVacante(jsonVacante.getInt("id_vacante"));
                    vacante.setNombre(jsonVacante.getString("nombre"));
                    vacante.setDescripcion(jsonVacante.optString("descripcion", "No disponible"));
                    vacante.setFecha(jsonVacante.optString("fecha", "No disponible"));
                    vacante.setSalario(jsonVacante.optDouble("salario", 0));
                    vacante.setEstatus(jsonVacante.getString("estatus"));

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

    public boolean crearVacante(Vacante vacante) {
        try {
            System.out.println("Este es el id " + this.id_empresa);
            URL url = new URL("http://localhost:8080/vacante/nuevavacante/" + this.id_empresa);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Construir el JSON
            String json = String.format(
                    "{ \"nombre\": \"%s\", \"descripcion\": \"%s\", \"salario\": " +
                            vacante.getSalario() + ", \"imagen\": \"%s\", \"detalles\": \"%s\" }",
                    vacante.getNombre(),
                    vacante.getDescripcion(),

                    vacante.getImagen(),
                    vacante.getDetalles()
            );

            System.out.println("Enviando solicitud con el siguiente JSON:");
            System.out.println(json);

            try (OutputStream escribir = connection.getOutputStream()) {
                byte[] mensaje = json.getBytes(StandardCharsets.UTF_8);
                escribir.write(mensaje, 0, mensaje.length);

                // Leer respuesta
                int respuesta = connection.getResponseCode();

                if (respuesta == HttpURLConnection.HTTP_OK || respuesta == HttpURLConnection.HTTP_CREATED) {
                    return true;
                } else {

                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            errorResponse.append(inputLine);
                        }
                        System.err.println("Error al crear vacante: " + errorResponse.toString());
                    }
                    return false;
                }

            } catch (MalformedURLException e) {
                System.err.println("Error en la URL: " + e.getMessage());
                return false;
            } catch (IOException e) {
                System.err.println("Error de IO: " + e.getMessage());
                return false;
            }

        } catch (ProtocolException e) {
            System.err.println("Error de protocolo: " + e.getMessage());
            return false;
        } catch (MalformedURLException e) {
            System.err.println("Error en la URL: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarVacante(int id_vacante, Vacante vacante) {

        try {
            System.out.println("Este es el id " + this.id_empresa);
            URL url = new URL("http://localhost:8080/vacante/modificar/" + id_vacante);  // Usamos el id_vacante en la URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);


            String json = String.format(
                    "{ \"nombre\": \"%s\", \"descripcion\": \"%s\", \"salario\": " + vacante.getSalario() + ", \"imagen\": \"%s\", \"detalles\": \"%s\" }",
                    vacante.getNombre(),
                    vacante.getDescripcion(),
                    vacante.getImagen(),
                    vacante.getDetalles()
            );

            System.out.println("Enviando solicitud con el siguiente JSON:");
            System.out.println(json);

            try (OutputStream escribir = connection.getOutputStream()) {
                byte[] mensaje = json.getBytes(StandardCharsets.UTF_8);
                escribir.write(mensaje, 0, mensaje.length);

                // Leer respuesta
                int respuesta = connection.getResponseCode();

                if (respuesta == HttpURLConnection.HTTP_OK || respuesta == HttpURLConnection.HTTP_CREATED) {
                    return true;
                } else {
                    // Si la respuesta no es OK o CREATED, leer la respuesta de error
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            errorResponse.append(inputLine);
                        }
                        System.err.println("Error al modificar vacante: " + errorResponse.toString());
                    }
                    return false;
                }

            } catch (MalformedURLException e) {
                System.err.println("Error en la URL: " + e.getMessage());
                return false;
            } catch (IOException e) {
                System.err.println("Error de IO: " + e.getMessage());
                return false;
            }

        } catch (ProtocolException e) {
            System.err.println("Error de protocolo: " + e.getMessage());
            return false;
        } catch (MalformedURLException e) {
            System.err.println("Error en la URL: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelarVacante(int id_vacante){
        try {
            System.out.println("Enviando solicitud para cancelar la vacante con ID: " + id_vacante);
            URL url = new URL("http://localhost:8080/vacante/retirar/" + id_vacante);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // No es necesario enviar un cuerpo en la solicitud PUT, ya que solo estamos retirando
            try (OutputStream escribir = connection.getOutputStream()) {
                byte[] mensaje = "{}".getBytes(StandardCharsets.UTF_8);  // Cuerpo vacío
                escribir.write(mensaje, 0, mensaje.length);

                // Leer respuesta
                int respuesta = connection.getResponseCode();

                if (respuesta == HttpURLConnection.HTTP_OK || respuesta == HttpURLConnection.HTTP_NO_CONTENT) {
                    return true;  // Vacante retirada con éxito
                } else {
                    // Si la respuesta no es OK o NO_CONTENT, leer la respuesta de error
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            errorResponse.append(inputLine);
                        }
                        System.err.println("Error al retirar vacante: " + errorResponse.toString());
                    }
                    return false;  // Falló la retirada de la vacante
                }

            } catch (MalformedURLException e) {
                System.err.println("Error en la URL: " + e.getMessage());
                return false;
            } catch (IOException e) {
                System.err.println("Error de IO: " + e.getMessage());
                return false;
            }

        } catch (ProtocolException e) {
            System.err.println("Error de protocolo: " + e.getMessage());
            return false;
        } catch (MalformedURLException e) {
            System.err.println("Error en la URL: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
            return false;
        }
    }

    public boolean asignarVacante(int id_vacante,int id_solicitud){
        try {
            System.out.println("Enviando solicitud para asignar la vacante con ID: " + id_vacante + " a la solicitud con ID: " + id_solicitud);
            URL url = new URL("http://localhost:8080/vacante/asignar/" + id_vacante + "/" + id_solicitud);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            /* No necesitamos enviar un mensaje ya que todo va en la url del endpoint solo necesitamos leer
            la respuesta*/
            try {

                // Leer respuesta

                int respuesta = connection.getResponseCode();

                if (respuesta == HttpURLConnection.HTTP_OK || respuesta == HttpURLConnection.HTTP_NO_CONTENT) {
                    return true;
                } else {
                    // Si la respuesta no es OK o NO_CONTENT, leer la respuesta de error
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            errorResponse.append(inputLine);
                        }
                        System.err.println("Error al asignar vacante: " + errorResponse.toString());
                    }
                    return false;
                }

            } catch (MalformedURLException e) {
                System.err.println("Error en la URL: " + e.getMessage());
                return false;
            } catch (IOException e) {
                System.err.println("Error de IO: " + e.getMessage());
                return false;
            }

        } catch (ProtocolException e) {
            System.err.println("Error de protocolo: " + e.getMessage());
            return false;
        } catch (MalformedURLException e) {
            System.err.println("Error en la URL: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
            return false;
        }
    }

    public List<Solicitud> obtenerSolicitudes(int id_vacante){
        try {
            URL url = new URL("http://localhost:8080/vacante/solicitudes/" + id_vacante);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int respuesta = connection.getResponseCode();

            if (respuesta == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    // Parsear el JSON usando Gson
                    /*TypeToken crea una clase anonima especializada en representar los tipos de datos que vienen en la solicitud
                    de esta manera conseguimos que la información no se pierda
                     */
                    Gson gson = new Gson();
                    List<Solicitud> solicitudes = gson.fromJson(response.toString(), new TypeToken<List<Solicitud>>(){}.getType());

                    return solicitudes;
                } catch (IOException e) {
                    System.err.println("Error al leer la respuesta: " + e.getMessage());
                    return null;
                }
            } else {
                System.err.println("Error al obtener solicitudes: " + respuesta);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
            return null;
        }


    }

    public boolean altaEmpresa(Empresa empresa) {
        try {
            URL url = new URL("http://localhost:8080/empresa/nuevo");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Construir el JSON
            Gson gson = new Gson();
            String json = gson.toJson(empresa);

            System.out.println("Enviando solicitud con el siguiente JSON:");
            System.out.println(json);

            try (OutputStream escribir = connection.getOutputStream()) {
                byte[] mensaje = json.getBytes(StandardCharsets.UTF_8);
                escribir.write(mensaje, 0, mensaje.length);

                // Leer respuesta
                int respuesta = connection.getResponseCode();

                if (respuesta == HttpURLConnection.HTTP_OK || respuesta == HttpURLConnection.HTTP_CREATED) {
                    return true;
                } else {

                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            errorResponse.append(inputLine);
                        }
                        System.err.println("Error al crear empresa: " + errorResponse.toString());
                    }
                    return false;
                }

            } catch (MalformedURLException e) {
                System.err.println("Error en la URL: " + e.getMessage());
                return false;
            } catch (IOException e) {
                System.err.println("Error de IO: " + e.getMessage());
                return false;
            }

        } catch (ProtocolException e) {
            System.err.println("Error de protocolo: " + e.getMessage());
            return false;
        } catch (MalformedURLException e) {
            System.err.println("Error en la URL: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
            return false;
        }
    }

    public List<Empresa> obtenerEmpresas() {
        List<Empresa> empresas = new ArrayList<>();

        try {
            URL url = new URL("http://localhost:8080/empresa");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json"); // Mejor usar Accept

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Error HTTP: " + connection.getResponseCode());
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {

                String jsonResponse = reader.lines().collect(Collectors.joining());
                JSONArray jsonArray = new JSONArray(jsonResponse);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonEmpresa = jsonArray.getJSONObject(i);
                    Empresa empresa = new Empresa();

                    // Manejo seguro de campos con posibles valores nulos o tipos incorrectos
                    empresa.setId_empresa(jsonEmpresa.optInt("id_empresa", 0));
                    empresa.setRazon_social(jsonEmpresa.optString("razon_social", ""));
                    empresa.setPais(jsonEmpresa.optString("pais", ""));

                    // Manejo especial para direccion_social
                    Object direccion = jsonEmpresa.opt("direccion_social");
                    if (direccion != null) {
                        empresa.setDireccion_social(direccion.toString());
                    } else {
                        empresa.setDireccion_social("");
                    }

                    empresas.add(empresa);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo empresas: " + e.getMessage(), e);
        }

        return empresas;
    }

    public Empresa obtenerEmpresaPorId(int id) {
        try {
            URL url = new URL("http://localhost:8080/empresa/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return null; // Empresa no encontrada
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Error HTTP: " + responseCode);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {

                String jsonResponse = reader.lines().collect(Collectors.joining());
                JSONObject jsonEmpresa = new JSONObject(jsonResponse);

                Empresa empresa = new Empresa();
                empresa.setId_empresa(jsonEmpresa.optInt("id_empresa", 0));
                empresa.setRazon_social(jsonEmpresa.optString("razon_social", ""));
                empresa.setDireccion_social(jsonEmpresa.optString("direccion_social", ""));
                empresa.setPais(jsonEmpresa.optString("pais", ""));

                return empresa;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo empresa: " + e.getMessage(), e);
        }
    }

    public boolean actualizarEmpresa(int id, Empresa empresa) {
        try {
            URL url = new URL("http://localhost:8080/empresa/modificar/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Crear JSON con los datos actualizados
            JSONObject jsonEmpresa = new JSONObject();
            jsonEmpresa.put("id_empresa", empresa.getId_empresa());
            jsonEmpresa.put("razon_social", empresa.getRazon_social());
            jsonEmpresa.put("direccion_social", empresa.getDireccion_social());
            jsonEmpresa.put("pais", empresa.getPais());

            // Escribir el JSON en el cuerpo de la solicitud
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonEmpresa.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando empresa: " + e.getMessage(), e);
        }
    }

    public boolean borrarEmpresa(int id) {
        try {
            URL url = new URL("http://localhost:8080/empresa/retirar/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            // Considerar diferentes códigos de éxito según tu API
            return responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_NO_CONTENT;

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando empresa: " + e.getMessage(), e);
        }
    }

    public boolean crearCategoria(Categoria categoria) {
        try {
            URL url = new URL("http://localhost:8080/categoria/nuevo");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Construir el JSON
            Gson gson = new Gson();
            String json = gson.toJson(categoria);

            System.out.println("Enviando solicitud con el siguiente JSON:");
            System.out.println(json);

            try (OutputStream escribir = connection.getOutputStream()) {
                byte[] mensaje = json.getBytes(StandardCharsets.UTF_8);
                escribir.write(mensaje, 0, mensaje.length);

                // Leer respuesta
                int respuesta = connection.getResponseCode();

                if (respuesta == HttpURLConnection.HTTP_OK || respuesta == HttpURLConnection.HTTP_CREATED) {
                    return true;
                } else {

                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            errorResponse.append(inputLine);
                        }
                        System.err.println("Error al crear vacante: " + errorResponse.toString());
                    }
                    return false;
                }

            } catch (MalformedURLException e) {
                System.err.println("Error en la URL: " + e.getMessage());
                return false;
            } catch (IOException e) {
                System.err.println("Error de IO: " + e.getMessage());
                return false;
            }

        } catch (ProtocolException e) {
            System.err.println("Error de protocolo: " + e.getMessage());
            return false;
        } catch (MalformedURLException e) {
            System.err.println("Error en la URL: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
            return false;
        }
    }

    public List<Categoria> obtenerCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        try {
            URL url = new URL("http://localhost:8080/categoria");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                JSONArray jsonArray = new JSONArray(reader.lines().collect(Collectors.joining()));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonCat = jsonArray.getJSONObject(i);
                    Categoria cat = new Categoria();
                    cat.setId_categoria(jsonCat.getInt("id_categoria"));
                    cat.setNombre(jsonCat.getString("nombre"));
                    cat.setDescripcion(jsonCat.optString("descripcion", ""));
                    categorias.add(cat);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo categorías: " + e.getMessage(), e);
        }
        return categorias;
    }

    public Categoria obtenerCategoriaPorId(int id) {
        try {
            URL url = new URL("http://localhost:8080/categoria/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                JSONObject jsonCat = new JSONObject(reader.lines().collect(Collectors.joining()));

                Categoria cat = new Categoria();
                cat.setId_categoria(jsonCat.getInt("id_categoria"));
                cat.setNombre(jsonCat.getString("nombre"));
                cat.setDescripcion(jsonCat.optString("descripcion", ""));
                return cat;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo categoría: " + e.getMessage(), e);
        }
    }

    public boolean actualizarCategoria(int id, Categoria categoria) {
        try {
            URL url = new URL("http://localhost:8080/categoria/modificar/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Crear JSON con los datos actualizados
            JSONObject jsonCategoria = new JSONObject();
            jsonCategoria.put("nombre", categoria.getNombre());
            jsonCategoria.put("descripcion", categoria.getDescripcion());

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonCategoria.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando categoría: " + e.getMessage(), e);
        }
    }

    public boolean borrarCategoria(int id) {
        try {
            URL url = new URL("http://localhost:8080/categoria/retirar/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            // Considerar diferentes códigos de éxito según tu API
            return responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_NO_CONTENT;

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando categoría: " + e.getMessage(), e);
        }
    }

    public boolean desactivarUsuario(String username) {
        try {
            URL url = new URL("http://localhost:8080/usuario/desactivar/" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            throw new RuntimeException("Error desactivando usuario: " + e.getMessage(), e);
        }
    }

    public List<Usuario> obtenerUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            URL url = new URL("http://localhost:8080/usuario");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                JSONArray jsonArray = new JSONArray(reader.lines().collect(Collectors.joining()));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonUser = jsonArray.getJSONObject(i);
                    Usuario usuario = new Usuario();
                    usuario.setUsername(jsonUser.getString("username"));
                    usuario.setActivado(jsonUser.getBoolean("activado"));
                    usuarios.add(usuario);
                }
            }
            return usuarios;

        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo usuarios: " + e.getMessage(), e);
        }
    }

    public boolean crearUsuario(int id_perfil, Usuario usuario) {
        try {
            URL url = new URL("http://localhost:8080/usuario/registro/" + id_perfil);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Construir el JSON
            String json = String.format(
                    "{ \"username\": \"%s\", \"nombre\": \"%s\", \"apellidos\": \"%s\", \"email\": \"%s\", \"password\": \"%s\" }",
                    usuario.getUsername(),
                    usuario.getNombre(),
                    usuario.getApellidos(),
                    usuario.getEmail(),
                    usuario.getPassword()

            );

            System.out.println("Enviando solicitud con el siguiente JSON:");
            System.out.println(json);

            try (OutputStream escribir = connection.getOutputStream()) {
                byte[] mensaje = json.getBytes(StandardCharsets.UTF_8);
                escribir.write(mensaje, 0, mensaje.length);

                // Leer respuesta
                int respuesta = connection.getResponseCode();

                if (respuesta == HttpURLConnection.HTTP_OK || respuesta == HttpURLConnection.HTTP_CREATED) {
                    return true;
                } else {

                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            errorResponse.append(inputLine);
                        }
                        System.err.println("Error al crear usuario: " + errorResponse.toString());
                    }
                    return false;
                }

            } catch (MalformedURLException e) {
                System.err.println("Error en la URL: " + e.getMessage());
                return false;
            } catch (IOException e) {
                System.err.println("Error de IO: " + e.getMessage());
                return false;
            }

        } catch (ProtocolException e) {
            System.err.println("Error de protocolo: " + e.getMessage());
            return false;
        } catch (MalformedURLException e) {
            System.err.println("Error en la URL: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
            return false;
        }
    }

    public boolean asignarAdmin(String username) {
        try {
            URL url = new URL("http://localhost:8080/usuario/asignar/" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            throw new RuntimeException("Error asignando admin: " + e.getMessage(), e);
        }
    }

}
