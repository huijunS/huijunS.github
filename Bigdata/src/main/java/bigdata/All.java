package julend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class All implements Serializable {

    public List<JsonDBName> getAll() {
        return all;
    }

    public void setAll(List<JsonDBName> all) {
        this.all = all;
    }

    private List<JsonDBName> all;
}
