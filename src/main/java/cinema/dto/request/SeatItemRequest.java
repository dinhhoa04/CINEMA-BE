package cinema.dto.request;

public class SeatItemRequest {
    private String rowLabel;  // A, B, C...
    private Byte colNumber;   // 1, 2, 3...
    private Byte seatTypeId;  // ID loại ghế (1: Thường, 2: VIP...)
    private Boolean isActive; // Trạng thái (false = Lối đi / Ghế hỏng)

    // Tự sinh Getter và Setter để không phụ thuộc vào Lombok
    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }

    public Byte getColNumber() { return colNumber; }
    public void setColNumber(Byte colNumber) { this.colNumber = colNumber; }

    public Byte getSeatTypeId() { return seatTypeId; }
    public void setSeatTypeId(Byte seatTypeId) { this.seatTypeId = seatTypeId; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}