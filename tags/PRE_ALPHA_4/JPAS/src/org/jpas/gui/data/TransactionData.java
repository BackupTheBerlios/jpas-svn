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
import java.util.Date;

/**
 * @author Justin W Smith
 *
 */
public class TransactionData
{
	private final Date date;
    private final String num;
    private final String payee;
    private final long withdraw;
    private final long deposit;
    private final Category[] categories; 
    private final String memo;

    
    public TransactionData(	final Date date,
							final String num,
						    final String payee,
						    final long withdraw,
						    final long deposit,
						    final Category[] categories, 
						    final String memo)
    {
		this.date = date;
        this.num = num;
        this.payee = payee;
        this.withdraw = withdraw;
        this.deposit = deposit;
        this.categories = categories;
        this.memo = memo;
    }

	public Date getDate()
	{
		return date;
	}
	
    public String getNum()
    {
        return num;
    }
    public String getPayee()
    {
        return payee;
    }
    public long getWithdraw()
    {
        return withdraw;
    }
    public long getDeposit()
    {
        return deposit;
    }
    public Category[] getCategories()
    {
        return categories;
    }
    public String getMemo()
    {
        return memo;
    }
    
    public String toString()
    {
		final StringBuffer buffer = new StringBuffer("TransactionData { " + num + ", "
        	+ payee + ", "
        	+ withdraw + ", "
        	+ deposit + ", " 
        	+ "{ ");
		
		for(int i = 0; i < categories.length; i++)
		{
        	buffer.append(categories[i].getName());
			if(i + 1 < categories.length)
			{
				buffer.append(", ");
			}
		}
		
		buffer.append("}, memo}");
		return buffer.toString();
    }
    
    public static void main(String[] args)
    {
    }
}
