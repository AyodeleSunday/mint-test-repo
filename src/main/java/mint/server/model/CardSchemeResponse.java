package mint.server.model;

public class CardSchemeResponse extends Response {
    private CardSchemePayload payload;

    public CardSchemePayload getPayload() {
        return payload;
    }

    public void setPayload(CardSchemePayload payload) {
        this.payload = payload;
    }
}
