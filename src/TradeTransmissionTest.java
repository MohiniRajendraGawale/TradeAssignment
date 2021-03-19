import static org.junit.Assert.assertTrue;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.Test;

public class TradeTransmissionTest {

	// Junit for new Trade Transmission, should return true as Trade transmitted
	// successfully

	@Test
	public void testTradeTransmission1()
			throws ClassNotFoundException, SQLException, LowerVersionTradeException, PastMaturityDate {

		Trade t1 = new Trade();
		t1.setTradeId("T3");
		t1.setVersion(1);
		t1.setCounter_PartyId("CP-1");
		t1.setBookId("B1");
		t1.setMaturityDate(LocalDate.of(2022, 05, 20));
		t1.setCreatedDate(LocalDate.now());
		t1.setExpired('N');
		assertTrue(TradeTransmission.tradeTransmission(t1));

	}

	// Junit for lower version Trade, should throw Exception
	// LowerVersionTradeException

	@Test(expected = LowerVersionTradeException.class)
	public void testTradeTransmission2()
			throws ClassNotFoundException, SQLException, LowerVersionTradeException, PastMaturityDate {
		Trade t1 = new Trade();
		t1.setTradeId("T4");
		t1.setVersion(1);
		t1.setCounter_PartyId("CP-1");
		t1.setBookId("B1");
		t1.setMaturityDate(LocalDate.of(2022, 05, 20));
		t1.setCreatedDate(LocalDate.now());
		t1.setExpired('N');

		Trade t4 = new Trade();
		t4.setTradeId("T4");
		t4.setVersion(0);
		t4.setCounter_PartyId("CP-2");
		t4.setBookId("B2");
		t4.setMaturityDate(LocalDate.of(2022, 05, 20));
		t4.setCreatedDate(LocalDate.now());
		t4.setExpired('N');
		TradeTransmission.tradeTransmission(t1);
		TradeTransmission.tradeTransmission(t4);
	}

	// Junit for same version Trade, the existing record should get updated
	// successfully and return true

	@Test
	public void testTradeTransmission3()
			throws ClassNotFoundException, SQLException, LowerVersionTradeException, PastMaturityDate {
		Trade t1 = new Trade();
		t1.setTradeId("T5");
		t1.setVersion(1);
		t1.setCounter_PartyId("CP-1");
		t1.setBookId("B1");
		t1.setMaturityDate(LocalDate.of(2022, 05, 20));
		t1.setCreatedDate(LocalDate.now());
		t1.setExpired('N');

		Trade t4 = new Trade();
		t4.setTradeId("T5");
		t4.setVersion(1);
		t4.setCounter_PartyId("CP-2");
		t4.setBookId("B2");
		t4.setMaturityDate(LocalDate.of(2022, 05, 20));
		t4.setCreatedDate(LocalDate.now());
		t4.setExpired('N');
		TradeTransmission.tradeTransmission(t1);
		assertTrue(TradeTransmission.tradeTransmission(t4));
	}

	// Junit for new Trade Transmission for past Maturity Date, store should reject
	// such transmission

	@Test(expected = PastMaturityDate.class)
	public void testTradeTransmission4()
			throws ClassNotFoundException, SQLException, LowerVersionTradeException, PastMaturityDate {
		Trade t1 = new Trade();
		t1.setTradeId("T6");
		t1.setVersion(1);
		t1.setCounter_PartyId("CP-1");
		t1.setBookId("B1");
		t1.setMaturityDate(LocalDate.of(2020, 05, 20));
		t1.setCreatedDate(LocalDate.now());
		t1.setExpired('N');

		TradeTransmission.tradeTransmission(t1);
	}
}
