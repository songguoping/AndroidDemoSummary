package com.sgp.shoppingcartdemo.bean;

import java.util.List;



public class GoodBean {

    /**
     * id : 1
     * adress : 郑州报税发货
     * isedit : false
     * gooddetail : [{"id":1,"pic":"www","count":1,"limitcount":2,"name":"BB霜","price":899,"isedit":false}]
     */

    private List<ContentBean> content;
    private int allmoney;
    private int allcount;
    private boolean isAllSelect;

    public boolean isAllSelect() {
        return isAllSelect;
    }

    public void setAllSelect(boolean allSelect) {
        isAllSelect = allSelect;
    }

    public int getAllmoney() {
        return allmoney;
    }

    public void setAllmoney(int allmoney) {
        this.allmoney = allmoney;
    }

    public int getAllcount() {
        return allcount;
    }

    public void setAllcount(int allcount) {
        this.allcount = allcount;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        private String id;
        private String adress;
        private boolean isselected;


        /**
         * id : 1
         * pic : www
         * count : 1
         * limitcount : 2
         * name : BB霜
         * price : 899
         * isedit : false
         */



        private List<GooddetailBean> gooddetail;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAdress() {
            return adress;
        }

        public void setAdress(String adress) {
            this.adress = adress;
        }

        public boolean isselected() {
            return isselected;
        }

        public void setIsselected(boolean isselected) {
            this.isselected = isselected;
        }

        public List<GooddetailBean> getGooddetail() {
            return gooddetail;
        }

        public void setGooddetail(List<GooddetailBean> gooddetail) {
            this.gooddetail = gooddetail;
        }

        public static class GooddetailBean {
            private String id;
            private String pic;
            private String count;
            private String name;
            private String price;
            private boolean isedit;
            private boolean isselected;

            public boolean isedit() {
                return isedit;
            }

            public boolean isselected() {
                return isselected;
            }

            public void setIsselected(boolean isselected) {
                this.isselected = isselected;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }


            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public boolean isIsedit() {
                return isedit;
            }

            public void setIsedit(boolean isedit) {
                this.isedit = isedit;
            }
        }
    }
}
