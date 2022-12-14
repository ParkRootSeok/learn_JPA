package practice.jpashop.domain;

import practice.jpashop.config.BaseEntity;
import section7.domain.Member;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Order extends BaseEntity {

    @Id @GeneratedValue
    @JoinColumn(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private Date orderDate;
    private String status;

}
