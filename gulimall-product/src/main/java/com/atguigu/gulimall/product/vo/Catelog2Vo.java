package com.atguigu.gulimall.product.vo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description
 * Catelog2Vo
 * <p>
 * Data
 * 2020/6/14-23:14
 *
 * @author zrx
 * @version 1.0
 */

public class Catelog2Vo {
    private final static Logger LOGGER = LoggerFactory.getLogger(Catelog2Vo.class);

    private String catalog1Id;

    private List<Catalog3Vo> catalog3List;

    private String id;

    private String name;

    public Catelog2Vo() {
    }

    public Catelog2Vo(String catalog1Id, List<Catalog3Vo> catalog3List, String id, String name) {
        this.catalog1Id = catalog1Id;
        this.catalog3List = catalog3List;
        this.id = id;
        this.name = name;
    }

    public static class Catalog3Vo {

        private String catalog2Id;

        private String id;

        private String name;

        public Catalog3Vo() {
        }

        public Catalog3Vo(String catalog2Id, String id, String name) {
            this.catalog2Id = catalog2Id;
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Catalog3Vo{" +
                    "catalog2Id='" + catalog2Id + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }

        public String getCatalog2Id() {
            return catalog2Id;
        }

        public void setCatalog2Id(String catalog2Id) {
            this.catalog2Id = catalog2Id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public String toString() {
        return "Catelog2Vo{" +
                "catalog1Id='" + catalog1Id + '\'' +
                ", catalog3List=" + catalog3List +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getCatalog1Id() {
        return catalog1Id;
    }

    public void setCatalog1Id(String catalog1Id) {
        this.catalog1Id = catalog1Id;
    }

    public List<Catalog3Vo> getCatalog3List() {
        return catalog3List;
    }

    public void setCatalog3List(List<Catalog3Vo> catalog3List) {
        this.catalog3List = catalog3List;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
