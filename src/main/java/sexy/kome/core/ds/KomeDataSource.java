package sexy.kome.core.ds;

import com.willer.common.ConfigHelper;

/**
 * Created by Hack on 2016/12/1.
 */
public class KomeDataSource {
    public static void main(String[] args) {
        System.out.println(ConfigHelper.get("KOME_X"));
    }
}
