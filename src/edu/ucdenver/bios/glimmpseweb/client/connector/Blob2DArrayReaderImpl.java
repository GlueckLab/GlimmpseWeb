package edu.ucdenver.bios.glimmpseweb.client.connector;

import name.pehl.piriti.json.client.AbstractJsonReader;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import edu.ucdenver.bios.webservice.common.domain.Blob2DArray;

public class Blob2DArrayReaderImpl extends AbstractJsonReader<Blob2DArray>
implements Blob2DArrayReader {

    public Blob2DArrayReaderImpl() {
        this.jsonRegistry.register(edu.ucdenver.bios.webservice.common.domain.Blob2DArray.class, this);
    }
    
    @Override
    protected Blob2DArray newModel(JSONObject context) {
        return new Blob2DArray();
    }

    @Override
    protected Blob2DArray readId(JSONObject jsonObject)
    {
        // IDs in JSON are not supported!
        return newModel(jsonObject);
    }

    @Override
    protected Blob2DArray readProperties(JSONObject context, Blob2DArray model) {
        if (context != null) 
        {
            JSONValue jsonValue = context.get("data");
            if (jsonValue != null) {
                JSONArray jsonArrayRows = jsonValue.isArray();
                if (jsonArrayRows != null) {
                    int rows = jsonArrayRows.size();
                    if (rows > 0) {
                        JSONArray firstRow = jsonArrayRows.get(0).isArray();
                        if (firstRow != null) {
                            int columns = firstRow.size();
                            double[][] data = new double[rows][columns];
                            for(int r = 0; r < rows; r++) {
                                JSONArray currentRow = jsonArrayRows.get(r).isArray();
                                for(int c = 0; c < columns; c++) {
                                    JSONNumber number = currentRow.get(c).isNumber();
                                    data[r][c] = number.doubleValue();
                                }
                            }
                            model.setData(data);
                        }
                    }
                }
            }
        }
        return model;
    }

    @Override
    protected Blob2DArray readIdRefs(JSONObject context, Blob2DArray model) {
        // TODO Auto-generated method stub
        return model;
    }

}
