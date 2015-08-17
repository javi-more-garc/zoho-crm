/**
 * 
 */
package com.jmg.zcs;

import java.util.List;
import java.util.Map;

/**
 * @author Javier Moreno Garcia
 *
 */
public interface LeadService {
    
    public void insertLeads(Map<String, String> parameters, List<Map<String, String>> values);

}
