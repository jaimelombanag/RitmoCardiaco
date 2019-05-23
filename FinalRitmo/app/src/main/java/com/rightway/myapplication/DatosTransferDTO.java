package com.rightway.myapplication;

public class DatosTransferDTO {

    private String funcion;
    private String nombre;
    private String valor;
    private String foto;
    private String idConcursante;
    private String apuesta;
    private String accion;


    public String getFuncion() {
        return funcion;
    }

    public void setFuncion(String funcion) {
        this.funcion = funcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getIdConcursante() {
        return idConcursante;
    }

    public void setIdConcursante(String idConcursante) {
        this.idConcursante = idConcursante;
    }



    public String getApuesta() {
        return apuesta;
    }

    public void setApuesta(String apuesta) {
        this.apuesta = apuesta;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }
}
