package section8.domain;

import section7.Config.BaseEntity;

import javax.persistence.*;

@Entity
public class Delivery extends BaseEntity {

    @Id @GeneratedValue
    @JoinColumn(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Orders order;

    private String city;
    private String street;
    private String zipcode;
    private String status;
}