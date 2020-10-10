package mint.server.model;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BinHit {
    @Id
    private String bin;
    private Integer hitCount;

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public Integer getHitCount() {
        return hitCount;
    }

    public void setHitCount(Integer hitCount) {
        this.hitCount = hitCount;
    }
}
