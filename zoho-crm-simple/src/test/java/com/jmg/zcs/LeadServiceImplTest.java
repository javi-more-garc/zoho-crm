/**
 * 
 */
package com.jmg.zcs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author Javier Moreno Garcia
 *
 */
public class LeadServiceImplTest {

    private LeadService service = new LeadServiceImpl();

    @Test
    public void testInsertLeads() {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("authtoken", "");

        List<Map<String, String>> values = new ArrayList<Map<String, String>>();

        Map<String, String> lead1 = new HashMap<String, String>();
       
        lead1.put("Lead Source", "Web Download");
        lead1.put("Company", "Lead 1 company");
        // example of a key that will be transformed
        lead1.put("first_name", "John");
        lead1.put("Last Name", "Smith");
        lead1.put("Email", "john@smith.com");
        lead1.put("Title", "Software developer");
        lead1.put("Phone", "1234567890");

        Map<String, String> lead2 = new HashMap<String, String>();
       
        lead2.put("Lead Source", "Web Download");
        lead2.put("Company", "Lead 2 company");
        lead2.put("First Name", "Peter");
        lead2.put("Last Name", "Johnson");
        lead2.put("Email", "peter@johnson.com");        
        lead2.put("Title", "Manager");
        lead2.put("Phone", "66666");
        
        values.add(lead1);
        values.add(lead2);

        // perform business
        service.insertLeads(parameters, values);

    }

}
