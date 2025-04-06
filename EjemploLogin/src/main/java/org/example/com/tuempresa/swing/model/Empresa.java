package org.example.com.tuempresa.swing.model;

public class Empresa {

    private int id_empresa;
    private String razon_social;
    private String direccion_social;
    private String pais;

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getRazon_social() {
        return razon_social;
    }

    public void setRazon_social(String razon_social) {
        this.razon_social = razon_social;
    }

    public String getDireccion_social() {
        return direccion_social;
    }

    public void setDireccion_social(String direccion) {
        this.direccion_social = direccion;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}
