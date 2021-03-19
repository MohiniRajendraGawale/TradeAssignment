import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class TradeTransmission {

	public static boolean tradeTransmission(Trade t)
			throws SQLException, ClassNotFoundException, LowerVersionTradeException, PastMaturityDate {
		Connection con = getDBConnction();
		Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		ArrayList<Integer> versions = new ArrayList<Integer>();

		if (t.getMaturityDate().isAfter(LocalDate.now())) {
			ResultSet rs = stmt.executeQuery("select version from Trade where Trade_ID='" + t.getTradeId() + "'");
			if (rs.next()) {
				rs.beforeFirst();
				while (rs.next()) {
					versions.add(rs.getInt(1));
				}
			} else {
				String q = "insert into Trade values('" + t.TradeId + "'," + t.version + ",'" + t.Counter_PartyId
						+ "','" + t.BookId + "',to_date('" + t.getMaturityDate() + "','YYYY-MM-DD')," + "to_date('"
						+ t.getMaturityDate() + "','YYYY-MM-DD')" + ",'" + t.expired + "')";
				stmt.executeUpdate(q);
				return true;
			}

			Collections.sort(versions);
			Collections.reverse(versions);
			for (int ver : versions) {
				if (t.getVersion() < versions.get(0)) {
					throw new LowerVersionTradeException(
							"Trade Transmission rejected as version is lower than available version for Trade "
									+ t.getTradeId());
				} else if (t.getVersion() == ver) {
					String q = "Update Trade set Trade_ID='" + t.getTradeId() + "',Version=" + t.getVersion()
							+ ",Counter_Part_Id='" + t.getCounter_PartyId() + "',Book_id='" + t.getBookId()
							+ "',Maturity_Date=to_date('" + t.getMaturityDate() + "','YYYY-MM-DD')"
							+ ",Created_Date=to_date('" + t.getCreatedDate() + "','YYYY-MM-DD')" + ",Expired='"
							+ t.getExpired() + "' where Trade_id='" + t.getTradeId() + "'" + "and Version='"
							+ t.getVersion() + "'";
					stmt.executeUpdate(q);
					break;
				} else if (t.getVersion() > versions.get(0)) {
					String q = "insert into Trade values('" + t.TradeId + "'," + t.version + ",'" + t.Counter_PartyId
							+ "','" + t.BookId + "',to_date('" + t.getMaturityDate() + "','YYYY-MM-DD')," + "to_date('"
							+ t.getCreatedDate() + "','YYYY-MM-DD')" + ",'" + t.expired + "')";
					stmt.executeUpdate(q);
					break;
				}
			}
		} else {
			throw new PastMaturityDate("Trade is already expired.");
		}
		stmt.execute("Commit");
		con.close();
		return true;
	}

	public static void houseKeepingStore() throws SQLException {
		Connection con = getDBConnction();
		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery("select * from Trade");

		ZoneId presentZone = ZoneId.systemDefault();
		LocalDate localDate = LocalDate.now();
		Date dt = Date.from(localDate.atStartOfDay(presentZone).toInstant());

		while (rs.next()) {
			if (rs.getDate(5).before(dt)) {
				String q = "Update Trade set Expired='Y' where Trade_id='" + rs.getString(1) + "'" + "and Version="
						+ rs.getInt(2);
				stmt.executeUpdate(q);
			}
		}
		con.close();
	}

	public static Connection getDBConnction() {
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

			con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "system123");

		} catch (Exception e) {
			System.out.println("DB connection failed..." + e);
		}

		return con;
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		try {
			// New trade transmission
			Trade t1 = new Trade();
			t1.setTradeId("T1");
			t1.setVersion(1);
			t1.setCounter_PartyId("CP-1");
			t1.setBookId("B1");
			t1.setMaturityDate(LocalDate.of(2022, 05, 20));
			t1.setCreatedDate(LocalDate.now());
			t1.setExpired('N');

			Trade t4 = new Trade();
			t4.setTradeId("T2");
			t4.setVersion(1);
			t4.setCounter_PartyId("CP-2");
			t4.setBookId("B2");
			t4.setMaturityDate(LocalDate.of(2022, 05, 20));
			t4.setCreatedDate(LocalDate.now());
			t4.setExpired('N');

			Trade t5 = new Trade();
			t5.setTradeId("T1");
			t5.setVersion(2);
			t5.setCounter_PartyId("CP-2");
			t5.setBookId("B2");
			t5.setMaturityDate(LocalDate.of(2022, 05, 20));
			t5.setCreatedDate(LocalDate.now());
			t5.setExpired('N');

			// New transmission for existing trade with same version number, existing trade
			// should get update
			Trade t2 = new Trade();
			t2.setTradeId("T1");
			t2.setVersion(2);
			t2.setCounter_PartyId("CP-2");
			t2.setBookId("B2");
			t2.setMaturityDate(LocalDate.of(2022, 05, 20));
			t2.setCreatedDate(LocalDate.now());
			t2.setExpired('N');

			// New transmission for existing trade with lower version number, it should
			// throw exception
			Trade t3 = new Trade();
			t3.setTradeId("T1");
			t3.setVersion(0);
			t3.setCounter_PartyId("CP-2");
			t3.setBookId("B2");
			t3.setMaturityDate(LocalDate.of(2021, 05, 20));
			t3.setCreatedDate(LocalDate.now());
			t3.setExpired('N');

			// New transmission with past maturity date, it should now allow and throw
			// exception
			Trade t6 = new Trade();
			t6.setTradeId("T3");
			t6.setVersion(1);
			t6.setCounter_PartyId("CP-1");
			t6.setBookId("B2");
			t6.setMaturityDate(LocalDate.of(2020, 05, 20));
			t6.setCreatedDate(LocalDate.now());
			t6.setExpired('N');

			if (tradeTransmission(t4)) {
				System.out.println("Trade Transmitted successfully..");
			} else
				System.out.println("Some Error occured..");

			if (tradeTransmission(t5)) {
				System.out.println("Trade Transmitted successfully..");
			} else
				System.out.println("Some Error occured..");

			if (tradeTransmission(t2)) {
				System.out.println("Trade Transmitted successfully..");
			} else
				System.out.println("Some Error occured..");

			if (tradeTransmission(t3)) {
				System.out.println("Trade Transmitted successfully..");
			} else
				System.out.println("Some Error occured..");

			if (tradeTransmission(t6)) {
				System.out.println("Trade Transmitted successfully..");
			} else
				System.out.println("Some Error occured..");

			// Houesekeeping activity for Trade store to update expired flag to 'Y' if
			// maturity date is crossed
			houseKeepingStore();

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
