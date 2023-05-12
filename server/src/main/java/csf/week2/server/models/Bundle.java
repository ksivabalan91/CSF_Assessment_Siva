package csf.week2.server.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bundle {
    public String bundleId;
    public String date;
    public String title;
    public String name;
    public String comments;
    public String[] urls;    
}
