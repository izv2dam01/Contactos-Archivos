package com.izv.dam.aadcurso1920app1.data;

import java.util.List;

public class Contacto {

    private long id;
    private String nombre;
    private List<String> telefonos = null;

    public Contacto(long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Contacto() {
        this(0, "");
    }

    public long getId() {
        return id;
    }

    public Contacto setId(long id) {
        this.id = id;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public Contacto setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public List<String> getTelefonos() {
        return telefonos;
    }

    public Contacto setTelefonos(List<String> telefonos) {
        this.telefonos = telefonos;
        return this;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", telefonos=" + telefonos +
                '}';
    }
}
