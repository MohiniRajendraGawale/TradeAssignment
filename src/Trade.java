import java.time.LocalDate;

class Trade {
	String TradeId;
	int version;
	String Counter_PartyId;
	String BookId;
	LocalDate MaturityDate;
	LocalDate CreatedDate;
	char expired;
	public String getTradeId() {
		return TradeId;
	}
	public void setTradeId(String tradeId) {
		TradeId = tradeId;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getCounter_PartyId() {
		return Counter_PartyId;
	}
	public void setCounter_PartyId(String counter_PartyId) {
		Counter_PartyId = counter_PartyId;
	}
	public String getBookId() {
		return BookId;
	}
	public void setBookId(String bookId) {
		BookId = bookId;
	}
	public LocalDate getMaturityDate() {
		return MaturityDate;
	}
	public void setMaturityDate(LocalDate localDate) {
		MaturityDate = localDate;
	}
	public LocalDate getCreatedDate() {
		return CreatedDate;
	}
	public void setCreatedDate(LocalDate createdDate) {
		CreatedDate = createdDate;
	}
	public char getExpired() {
		return expired;
	}
	public void setExpired(char expired) {
		this.expired = expired;
	}
	
}
