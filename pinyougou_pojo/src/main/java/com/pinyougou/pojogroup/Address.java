package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;

import java.io.Serializable;

public class Address implements Serializable {
    private TbAddress address;  //基础地址信息

    private TbProvinces provinces; //省份

    private TbCities cities; //城市信息

    private TbAreas areas; //区县

    @Override
    public String toString() {
        return "Address{" +
                "address=" + address +
                ", provinces=" + provinces +
                ", cities=" + cities +
                ", areas=" + areas +
                '}';
    }

    public TbAddress getAddress() {
        return address;
    }

    public void setAddress(TbAddress address) {
        this.address = address;
    }

    public TbProvinces getProvinces() {
        return provinces;
    }

    public void setProvinces(TbProvinces provinces) {
        this.provinces = provinces;
    }

    public TbCities getCities() {
        return cities;
    }

    public void setCities(TbCities cities) {
        this.cities = cities;
    }

    public TbAreas getAreas() {
        return areas;
    }

    public void setAreas(TbAreas areas) {
        this.areas = areas;
    }
}
