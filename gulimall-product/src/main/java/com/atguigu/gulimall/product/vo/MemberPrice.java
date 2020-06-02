/**
  * Copyright 2020 bejson.com 
  */
package com.atguigu.gulimall.product.vo;

import java.math.BigDecimal;

/**
 * Auto-generated: 2020-06-02 22:24:14
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}