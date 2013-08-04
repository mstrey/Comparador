package br.nom.strey.maicon.comparador;

import java.util.Date;

public class DiferencaEm {
	
	/** 
     * Calcula a diferen�a de duas datas em meses 
     * <br> 
     * <b>Importante:</b> Quando realiza a diferen�a em meses entre duas datas, este m�todo considera as horas restantes e as converte em fra��o de meses. 
     * @param dataInicial 
     * @param dataFinal 
     * @return quantidade de meses existentes entre a dataInicial e dataFinal. 
     */  
    public static int meses(Date dataInicial, Date dataFinal){  
        int result = 0;  
        long diferenca = dataFinal.getTime() - dataInicial.getTime();  
        double diferencaEmMeses = (diferenca /1000) / 60 / 60 / 24 / 30; //resultado � diferen�a entre as datas em meses  
        //long diasRestantes = (diferenca /1000) / 60 / 60 %24; //calcula as horas restantes  
        //result = diferencaEmMeses + (diasRestantes /24d); //transforma as horas restantes em fra��o de dias  
        result = (int) diferencaEmMeses;  
        
        return result;  
    }  
      
	/** 
     * Calcula a diferen�a de duas datas em dias 
     * <br> 
     * <b>Importante:</b> Quando realiza a diferen�a em dias entre duas datas, este m�todo considera as horas restantes e as converte em fra��o de dias. 
     * @param dataInicial 
     * @param dataFinal 
     * @return quantidade de dias existentes entre a dataInicial e dataFinal. 
     */  
    public static int dias(Date dataInicial, Date dataFinal){  
    	int result = 0;  
        long diferenca = dataFinal.getTime() - dataInicial.getTime();  
        double diferencaEmDias = (diferenca /1000) / 60 / 60 /24; //resultado � diferen�a entre as datas em dias  
        //long horasRestantes = (diferenca /1000) / 60 / 60 %24; //calcula as horas restantes  
        //result = diferencaEmDias + (horasRestantes /24d); //transforma as horas restantes em fra��o de dias  
        result = (int) diferencaEmDias;
        
        return result;  
    }  
      
    /** 
     * Calcula a diferen�a de duas datas em horas 
     * <br> 
     * <b>Importante:</b> Quando realiza a diferen�a em horas entre duas datas, este m�todo considera os minutos restantes e os converte em fra��o de horas. 
     * @param dataInicial 
     * @param dataFinal 
     * @return quantidade de horas existentes entre a dataInicial e dataFinal. 
     */  
    public static int horas(Date dataInicial, Date dataFinal){  
        int result = 0;  
        long diferenca = dataFinal.getTime() - dataInicial.getTime();  
        long diferencaEmHoras = (diferenca /1000) / 60 / 60;  
        //long minutosRestantes = (diferenca / 1000)/60 %60;  
        //double horasRestantes = minutosRestantes / 60d;  
        //result = diferencaEmHoras + (horasRestantes);  
        result = (int) diferencaEmHoras;
        
        return result;  
    }  
      
    /** 
     * Calcula a diferen�a de duas datas em minutos 
     * <br> 
     * <b>Importante:</b> Quando realiza a diferen�a em minutos entre duas datas, este m�todo considera os segundos restantes e os converte em fra��o de minutos. 
     * @param dataInicial 
     * @param dataFinal 
     * @return quantidade de minutos existentes entre a dataInicial e dataFinal. 
     */  
    public static int minutos(Date dataInicial, Date dataFinal){  
        int result = 0;  
        long diferenca = dataFinal.getTime() - dataInicial.getTime();  
        double diferencaEmMinutos = (diferenca /1000) / 60; //resultado � diferen�a entre as datas em minutos  
        //long segundosRestantes = (diferenca / 1000)%60; //calcula os segundos restantes  
        //result = diferencaEmMinutos + (segundosRestantes /60d); //transforma os segundos restantes em minutos  
        result = (int) diferencaEmMinutos;
        
        return result;  
    }  
	
}