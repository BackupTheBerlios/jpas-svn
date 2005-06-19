package org.jpas.model;

import org.jpas.util.*;

public interface Category extends JpasObservable
{
    public void commit();
	
    public void reload();
    
    public void setName(final String name);

    public long getTotal();

    public boolean canBeDeleted();
    
    public boolean isDeleted();

    public boolean isLoaded();

    public boolean isTranfer();
    
    public boolean isUnknown();
    
    public String getCategoryName();
    
}
