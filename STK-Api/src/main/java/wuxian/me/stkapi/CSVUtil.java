package wuxian.me.stkapi;

import org.apache.commons.csv.*;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wuxian on 27/10/2018.
 */
public class CSVUtil {

    private CSVUtil() {

    }

    private final static String NEW_LINE_SEPARATOR = "\n";

    public static void writeTodayAllItems(String file, List<TodayAll.Item> items) {

        if (items.size() == 0) {
            return;
        }

        try {
            CSVFormat formator = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
            FileWriter fileWriter = new FileWriter(file);

            CSVPrinter printer = new CSVPrinter(fileWriter, formator);
            String[] headers = new String[]{"code", "name", "changepercent", "trade"
                    , "open", "high", "low", "settlement", "volume", "turnoverratio", "amount", "per", "pb", "mktcap", "nmc"};

            printer.printRecord(headers);
            for (int i = 0; i < items.size(); i++) {
                TodayAll.Item item = items.get(i);

                String[] tmp = new String[]{
                        item.code, item.name, item.changepercent, item.trade, item.open, item.high, item.low, item.settlement,
                        String.valueOf(item.volume), item.turnoverratio, item.amount, item.per, item.pb, item.mktcap, item.nmc
                };
                printer.printRecord(tmp);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
