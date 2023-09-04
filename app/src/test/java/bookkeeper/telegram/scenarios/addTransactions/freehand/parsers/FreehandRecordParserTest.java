package bookkeeper.telegram.scenarios.addTransactions.freehand.parsers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class FreehandRecordParserTest {
    private final FreehandRecordParser parser = new FreehandRecordParser();

    @Test
    void parseOk_1() throws ParseException {
        var record = parser.parse("еда 220");

        var referenceRecord = new FreehandRecord();

        referenceRecord.description = "еда";
        referenceRecord.amount = new BigDecimal("220");

        assertEquals(referenceRecord, record);
    }

    @Test
    void parseOk_2() throws ParseException {
        var record = parser.parse("пополнение +220");

        var referenceRecord = new FreehandRecord();

        referenceRecord.description = "пополнение";
        referenceRecord.amount = new BigDecimal("-220");  // negated expense = profit

        assertEquals(referenceRecord, record);
    }
}