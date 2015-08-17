/**
 * 
 */
package com.jmg.zcs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Javier Moreno Garcia
 *
 */
public class LeadServiceImpl implements LeadService {

    private static Logger logger = Logger.getLogger(LeadServiceImpl.class.getName());

    private static final String URL_INSERT = "https://crm.zoho.com/crm/private/xml/Leads/insertRecords";
    private static final Integer MAX_LEADS_PER_REQUEST = 200;

    private HttpClient client;

    private Map<String, String> mapDefaultParameters;

    private Map<String, String> mapTransformationKeys;

    public LeadServiceImpl() {
        client = HttpClientBuilder.create().build();

        // default parameters

        mapDefaultParameters = new HashMap<String, String>();
        mapDefaultParameters.put("scope", "crmapi");
        mapDefaultParameters.put("newFormat", "1");
        mapDefaultParameters.put("duplicateCheck", "2");
        mapDefaultParameters.put("version", "4");

        // transformation keys

        mapTransformationKeys = new HashMap<String, String>();

        // put here whatever values you find convenient
        // this is just an example that will send as "First Name" values
        // received with
        // key "first_name"
        mapTransformationKeys.put("first_name", "First Name");

    }

    public void insertLeads(Map<String, String> parameters, List<Map<String, String>> values) {

        List<NameValuePair> commonParameters = new ArrayList<NameValuePair>();

        // add default parameters
        for (Entry<String, String> entryParam : mapDefaultParameters.entrySet()) {
            commonParameters.add(new BasicNameValuePair(entryParam.getKey(), entryParam.getValue()));
        }

        // use passed parameters

        for (Entry<String, String> entryParam : parameters.entrySet()) {
            commonParameters.add(new BasicNameValuePair(entryParam.getKey(), entryParam.getValue()));

        }

        // use passed values

        // partition in list of MAX_LEADS_PER_REQUEST elements
        List<List<Map<String, String>>> partitions = partition(values, MAX_LEADS_PER_REQUEST);

        // iterate partition
        for (List<Map<String, String>> partition : partitions) {

            // get xml for the current partition of leads data
            String dataXml = generateXML(partition);

            // create parameter lits using common parameters
            List<NameValuePair> finalParameters = new ArrayList<NameValuePair>(commonParameters);

            // add the xml data to the parameters
            finalParameters.add(new BasicNameValuePair("xmlData", dataXml));

            try {

                // instantiate post
                HttpPost post = new HttpPost(URL_INSERT);

                // add parameters
                post.setEntity(new UrlEncodedFormEntity(finalParameters));
                
                ResponseHandler<String> responseHandler= new BasicResponseHandler();

                logger.info(String.format("Sending: [%s]", finalParameters.toString()));
                
                // send request
                String response = client.execute(post,responseHandler);
                
                logger.info(String.format("Received: [%s]", response));
                
                
            } catch (Exception e) {
                // handle gracefully
                throw new RuntimeException(e);
            }
        }

    }

    //
    // private methods

    public static <T> List<List<T>> partition(Iterable<T> in, int size) {
        List<List<T>> lists = new ArrayList<List<T>>();
        Iterator<T> i = in.iterator();
        while (i.hasNext()) {
            List<T> list = new ArrayList<T>();
            for (int j = 0; i.hasNext() && j < size; j++) {
                list.add(i.next());
            }
            lists.add(list);
        }
        return lists;
    }

    private String generateXML(List<Map<String, String>> values) {
        StringBuilder builder = new StringBuilder();
        builder.append("<Leads>");

        int numRow = 1;

        // iterate through each lead
        for (Map<String, String> leadMap : values) {

            builder.append(String.format("<row no=\"%d\">", numRow));

            // iterate through each lead properties
            for (Entry<String, String> entryProperty : leadMap.entrySet()) {

                // get original key
                String originalKey = entryProperty.getKey();

                // see if we want to actually send this key using another key
                String transformedKey = mapTransformationKeys.get(originalKey);

                // get the final key to use
                String finalKey = transformedKey == null ? originalKey : transformedKey;

                // put property and its value
                builder.append(String.format("<FL val=\"%s\">%s</FL>", finalKey, entryProperty.getValue()));
            }

            builder.append("</row>");

            numRow++;

        }

        builder.append("</Leads>");

        return builder.toString();
    }

}
