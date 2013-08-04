package br.nom.strey.maicon.comparador;

import java.util.Date;

public class DiferencaEm {
	
	/** 
     * Calcula a diferença de duas datas em meses 
     * <br> 
     * <b>Importante:</b> Quando realiza a diferença em meses entre duas datas, este método considera as horas restantes e as converte em fração de meses. 
     * @param dataInicial 
     * @param dataFinal 
     * @return quantidade de meses existentes entre a dataInicial e dataFinal. 
     */  
    public static int meses(Date dataInicial, Date dataFinal){  
        int result = 0;  
        long diferenca = dataFinal.getTime() - dataInicial.getTime();  
        double diferencaEmMeses = (diferenca /1000) / 60 / 60 / 24 / 30; //resultado é diferença entre as datas em meses  
        //long diasRestantes = (diferenca /1000) / 60 / 60 %24; //calcula as horas restantes  
        //result = diferencaEmMeses + (diasRestantes /24d); //transforma as horas restantes em fração de dias  
        result = (int) diferencaEmMeses;  
        
        return result;  
    }  
      
	/** 
     * Calcula a diferença de duas datas em dias 
     * <br> 
     * <b>Importante:</b> Quando realiza a diferença em dias entre duas datas, este método considera as horas restantes e as converte em fração de dias. 
     * @param dataInicial 
     * @param dataFinal 
     * @return quantidade de dias existentes entre a dataInicial e dataFinal. 
     */  
    public static int dias(Date dataInicial, Date dataFinal){  
    	int result = 0;  
        long diferenca = dataFinal.getTime() - dataInicial.getTime();  
        double diferencaEmDias = (diferenca /1000) / 60 / 60 /24; //resultado é diferença entre as datas em dias  
        //long horasRestantes = (diferenca /1000) / 60 / 60 %24; //calcula as horas restantes  
        //result = diferencaEmDias + (horasRestantes /24d); //transforma as horas restantes em fração de dias  
        result = (int) diferencaEmDias;
        
        return result;  
    }  
      
    /** 
     * Calcula a diferença de duas datas em horas 
     * <br> 
     * <b>Importante:</b> Quando realiza a diferença em horas entre duas datas, este método considera os minutos restantes e os converte em fração de horas. 
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
     * Calcula a diferença de duas datas em minutos 
     * <br> 
     * <b>Importante:</b> Quando realiza a diferença em minutos entre duas datas, este método considera os segundos restantes e os converte em fração de minutos. 
     * @param dataInicial 
     * @param dataFinal 
     * @return quantidade de minutos existentes entre a dataInicial e dataFinal. 
     */  
    public static int minutos(Date dataInicial, Date dataFinal){  
        int result = 0;  
        long diferenca = dataFinal.getTime() - dataInicial.getTime();  
        double diferencaEmMinutos = (diferenca /1000) / 60; //resultado é diferença entre as datas em minutos  
        //long segundosRestantes = (diferenca / 1000)%60; //calcula os segundos restantes  
        //result = diferencaEmMinutos + (segundosRestantes /60d); //transforma os segundos restantes em minutos  
        result = (int) diferencaEmMinutos;
        
        return result;  
    }  
	
}