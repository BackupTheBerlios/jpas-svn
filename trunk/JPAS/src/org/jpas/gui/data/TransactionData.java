/**
 * Created on Nov 13, 2004 - 1:28:06 PM
 * 
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004 Justin W Smith
 * @author Justin W Smith
 * @version 1.0
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.jpas.gui.data;

import org.jpas.model.*;

/**
 * @author Justin W Smith
 *
 */
public class TransactionData
{
    private final String num;
    private final String payee;
    private final String withdraw;
    private final String deposit;
    private final Category category; 
    private final String memo;

    
    public TransactionData(	final String num,
						    final String payee,
						    final String withdraw,
						    final String deposit,
						    final Category category, 
						    final String memo)
    {
        this.num = num;
        this.payee = payee;
        this.withdraw = withdraw;
        this.deposit = deposit;
        this.category = category;
        this.memo = memo;
    }

    public String getNum()
    {
        return num;
    }
    public String getPayee()
    {
        return payee;
    }
    public String getWithdraw()
    {
        return withdraw;
    }
    public String getDeposit()
    {
        return deposit;
    }
    public Category getCategory()
    {
        return category;
    }
    public String getMemo()
    {
        return memo;
    }
    
    public String toString()
    {
        return "TransactionData { " + num + ", "
        	+ payee + ", "
        	+ withdraw + ", "
        	+ deposit + ", "
        	+ category + ", "
        	+ memo + "}";
    }
    
    public static void main(String[] args)
    {
    }
}
