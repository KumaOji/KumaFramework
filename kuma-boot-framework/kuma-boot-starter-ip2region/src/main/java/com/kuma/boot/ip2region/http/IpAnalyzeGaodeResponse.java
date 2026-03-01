/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.ip2region.http;

/**
 * @description 高德地图ip定位响应类
 */
public class IpAnalyzeGaodeResponse {

    /**
     * 返回结果状态值,值为0或1,0表示失败；1表示成功
     */
    private String status;

    /**
     * 返回状态说明，status为0时，info返回错误原因，否则返回“OK
     */
    private String info;

    /**
     * 返回状态说明,10000代表正确,详情参阅info状态表
     */
    private String infocode;

    /**
     * 若为直辖市则显示直辖市名称；
     *
     * 如果在局域网 IP网段内，则返回“局域网”；
     *
     * 非法IP以及国外IP则返回空
     */
    private String province;

    /**
     * 若为直辖市则显示直辖市名称；
     *
     * 如果为局域网网段内IP或者非法IP或国外IP，则返回空
     */
    private String city;

    /**
     * 城市的adcode编码
     */
    private String adcode;

    /**
     * 所在城市矩形区域范围，所在城市范围的左下右上对标对，如：116.0119343,39.66127144;116.7829835,40.2164962
     */
    private String rectangle;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getRectangle() {
        return rectangle;
    }

    public void setRectangle(String rectangle) {
        this.rectangle = rectangle;
    }
}
