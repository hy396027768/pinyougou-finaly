package entity;


import java.io.Serializable;
import java.math.BigDecimal;

public class OrderCount implements Serializable {
    private String  goodsName;  //商品数据
    private Integer count;  //销售数量
    private BigDecimal saleRoom; //销售额
    private String status; //商品状态
    private String sellerId;//商家id

    @Override
    public String toString() {
        return "OrderCount{" +
                "goodsName='" + goodsName + '\'' +
                ", count=" + count +
                ", saleRoom=" + saleRoom +
                ", status='" + status + '\'' +
                ", sellerId='" + sellerId + '\'' +
                '}';
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getSaleRoom() {
        return saleRoom;
    }

    public void setSaleRoom(BigDecimal saleRoom) {
        this.saleRoom = saleRoom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
