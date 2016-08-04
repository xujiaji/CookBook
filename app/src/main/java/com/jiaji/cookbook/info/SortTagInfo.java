package com.jiaji.cookbook.info;

import java.util.List;

/**
 * Created by JiaJi on 2015/12/12.
 */
public class SortTagInfo {
    private String resultcode;
    private String reason;
    private List<Result> result;
    public static class Result
    {
        private String parentId;
        private String name;
        private List<SubList> list;
        public static class SubList
        {
            private String id;
            private String name;
            private String parentId;

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

            public String getParentId() {
                return parentId;
            }

            public void setParentId(String parentId) {
                this.parentId = parentId;
            }
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<SubList> getList() {
            return list;
        }

        public void setList(List<SubList> subLists) {
            this.list = list;
        }
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

}
