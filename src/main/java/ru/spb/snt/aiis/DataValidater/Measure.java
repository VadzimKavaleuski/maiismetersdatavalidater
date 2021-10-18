/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.snt.aiis.DataValidater;


public class Measure {
    long id;
    String name;
    long last_correct_date;

    public Measure(long id, String name, long last_correct_date) {
        this.id = id;
        this.name = name;
        this.last_correct_date = last_correct_date;
    }


    
}
