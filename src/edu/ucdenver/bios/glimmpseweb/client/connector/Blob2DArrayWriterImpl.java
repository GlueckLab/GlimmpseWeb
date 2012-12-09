package edu.ucdenver.bios.glimmpseweb.client.connector;

import java.util.List;

import com.google.gwt.core.client.GWT;

import name.pehl.piriti.json.client.AbstractJsonWriter;
import name.pehl.piriti.json.client.JsonWriter;
import edu.ucdenver.bios.webservice.common.domain.Blob2DArray;

public class Blob2DArrayWriterImpl extends AbstractJsonWriter<edu.ucdenver.bios.webservice.common.domain.Blob2DArray> 
implements Blob2DArrayWriter 
{

    public interface Blob2DArrayWriter extends JsonWriter<Blob2DArray> {}
    
    public Blob2DArrayWriterImpl() 
    {
        this.jsonRegistry.register(edu.ucdenver.bios.webservice.common.domain.Blob2DArray.class, this);
    }

    @Override
    public String toJson(List<Blob2DArray> models, String arrayKey) {
        // TODO
        return null;
    }

    @Override
    public String toJson(Blob2DArray model) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{\"data\":[" );
        double[][] data = model.getData();
        int rows = data.length;
        int columns = data[0].length;
        for(int r = 0; r < rows; r++) {
            if (r > 0) {
                buffer.append(",");
            }
            buffer.append("[");
            for(int c = 0; c < columns; c++) {
                if (c > 0) {
                    buffer.append(",");
                }
                buffer.append(data[r][c]);
            }
            buffer.append("]");

        }
        buffer.append("]}");
        return buffer.toString();
    }

//    @Override
//    public HandlerRegistration addModelWriteHandler(
//            ModelWriteHandler<Blob2DArray> handler) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    @Override
//    public void fireEvent(GwtEvent<?> event) {
//        // TODO Auto-generated method stub
//        
//    }
}
