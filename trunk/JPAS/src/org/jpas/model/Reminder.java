/*
 * Created on Sep 26, 2004
 *
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004
 * License: Distributed under the terms of the GPL v2
 * @author Justin Smith
 * @version 1.0
 */

package org.jpas.model;

/**
 * @author jsmith
 *
 */
public class Reminder
{
    private final Integer id;
    
    private Integer accountID;
    private String payee;
    private String memo;
    private long amount;
    
    public enum AmountMethod 
    {
        //fixed(0), average(1), last(2);
    }
    
    Reminder(final Integer id)
    {
        this.id = id;
    }
    
    public static void main(String[] args)
    {
    }
}
