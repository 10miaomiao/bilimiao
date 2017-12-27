package com.a10miaomiao.bilimiao.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcc on 2016/10/11 18:20
 * 100332338@qq.com
 * <p>
 * 分区数据模型类
 */

public class RegionTypesInfo implements Parcelable {

  /**
   * code : 0
   * data : [{"tid":13,"reid":0,"name":"番剧","logo":"","goto":"","param":"","children":[{"tid":33,"reid":13,"name":"连载动画","logo":"http://i0.hdslb.com/u_user/54f589cd0573f9ab5c735698ae156d19.png","goto":"","param":""},{"tid":32,"reid":13,"name":"完结动画","logo":"http://i0.hdslb.com/u_user/18ad593e4b2f90b233f817e028bee71d.png","goto":"","param":""},{"tid":153,"reid":13,"name":"国产动画","logo":"http://i0.hdslb.com/u_user/405774aed11d0538a3548109a598fd80.png","goto":"","param":""},{"tid":51,"reid":13,"name":"资讯","logo":"","goto":"","param":""},{"tid":152,"reid":13,"name":"官方延伸","logo":"http://i0.hdslb.com/u_user/a78fa47e8f25772d51db1a19fe8b310f.png","goto":"","param":""}]},{"tid":1,"reid":0,"name":"动画","logo":"","goto":"","param":"","children":[{"tid":24,"reid":1,"name":"MAD·AMV","logo":"","goto":"","param":""},{"tid":25,"reid":1,"name":"MMD·3D","logo":"","goto":"","param":""},{"tid":47,"reid":1,"name":"短片·手书·配音","logo":"","goto":"","param":""},{"tid":27,"reid":1,"name":"综合","logo":"","goto":"","param":""}]},{"tid":3,"reid":0,"name":"音乐","logo":"","goto":"","param":"","children":[{"tid":31,"reid":3,"name":"翻唱","logo":"","goto":"","param":""},{"tid":30,"reid":3,"name":"VOCALOID·UTAU","logo":"","goto":"","param":""},{"tid":59,"reid":3,"name":"演奏","logo":"","goto":"","param":""},{"tid":54,"reid":3,"name":"OP/ED/OST","logo":"","goto":"","param":""},{"tid":28,"reid":3,"name":"原创音乐","logo":"","goto":"","param":""},{"tid":29,"reid":3,"name":"三次元音乐","logo":"","goto":"","param":""},{"tid":130,"reid":3,"name":"音乐选集","logo":"","goto":"","param":""}]},{"tid":129,"reid":0,"name":"舞蹈","logo":"","goto":"","param":"","children":[{"tid":20,"reid":129,"name":"宅舞","logo":"","goto":"","param":""},{"tid":154,"reid":129,"name":"三次元舞蹈","logo":"","goto":"","param":""},{"tid":156,"reid":129,"name":"舞蹈教程","logo":"http://i0.hdslb.com/u_user/f2f446184c967b47dd8ceb19e8ad634c.png","goto":"","param":""}]},{"tid":4,"reid":0,"name":"游戏","logo":"","goto":"","param":"","children":[{"tid":17,"reid":4,"name":"单机联机","logo":"","goto":"","param":""},{"tid":65,"reid":4,"name":"网游·电竞","logo":"","goto":"","param":""},{"tid":136,"reid":4,"name":"音游","logo":"","goto":"","param":""},{"tid":19,"reid":4,"name":"Mugen","logo":"","goto":"","param":""},{"tid":121,"reid":4,"name":"GMV","logo":"","goto":"","param":""}]},{"tid":36,"reid":0,"name":"科技","logo":"","goto":"","param":"","children":[{"tid":37,"reid":36,"name":"纪录片","logo":"","goto":"","param":""},{"tid":124,"reid":36,"name":"趣味科普人文","logo":"","goto":"","param":""},{"tid":122,"reid":36,"name":"野生技术协会","logo":"","goto":"","param":""},{"tid":39,"reid":36,"name":"演讲·公开课","logo":"","goto":"","param":""},{"tid":96,"reid":36,"name":"星海","logo":"","goto":"","param":""},{"tid":95,"reid":36,"name":"数码","logo":"","goto":"","param":""},{"tid":98,"reid":36,"name":"机械","logo":"","goto":"","param":""}]},{"tid":160,"reid":0,"name":"生活","logo":"http://i0.hdslb.com/bfs/archive/c2387196e2a4d1e7b2d77c6774ff398005e4953f.png","goto":"","param":"","children":[{"tid":138,"reid":160,"name":"搞笑","logo":"","goto":"","param":""},{"tid":21,"reid":160,"name":"日常","logo":"","goto":"","param":""},{"tid":76,"reid":160,"name":"美食圈","logo":"","goto":"","param":""},{"tid":75,"reid":160,"name":"动物圈","logo":"","goto":"","param":""},{"tid":161,"reid":160,"name":"手工","logo":"http://i0.hdslb.com/bfs/archive/f87bb34913e8f7eeef216aba813961c47117e783.png","goto":"","param":""},{"tid":162,"reid":160,"name":"绘画","logo":"http://i0.hdslb.com/bfs/archive/e6b66a76eb07f2acffd00b8f8c1cc0ff57e75e53.png","goto":"","param":""},{"tid":163,"reid":160,"name":"运动","logo":"http://i0.hdslb.com/bfs/archive/5cfa7ac649cc6b292e876a483062c04c4a2d9b6c.png","goto":"","param":""}]},{"tid":119,"reid":0,"name":"鬼畜","logo":"","goto":"","param":"","children":[{"tid":22,"reid":119,"name":"鬼畜调教","logo":"","goto":"","param":""},{"tid":26,"reid":119,"name":"音MAD","logo":"","goto":"","param":""},{"tid":126,"reid":119,"name":"人力VOCALOID","logo":"","goto":"","param":""},{"tid":127,"reid":119,"name":"教程演示","logo":"","goto":"","param":""}]},{"tid":155,"reid":0,"name":"时尚","logo":"http://i0.hdslb.com/u_user/abea372535e68ce4206f8bad68741380.png","goto":"","param":"","children":[{"tid":157,"reid":155,"name":"美妆","logo":"http://i0.hdslb.com/u_user/6c6b75e7fa62b5a9711676aa1d58d40d.png","goto":"","param":""},{"tid":158,"reid":155,"name":"服饰","logo":"http://i0.hdslb.com/u_user/49ae98cf31cf190f8df27fdd665839ba.png","goto":"","param":""},{"tid":159,"reid":155,"name":"资讯","logo":"http://i0.hdslb.com/u_user/909e5df75af68fc953d8dc847f7918e9.png","goto":"","param":""},{"tid":164,"reid":155,"name":"健身","logo":"http://i0.hdslb.com/bfs/archive/c5da2d170056227118594ab2c70d40ad9d0eed5c.png","goto":"","param":""}]},{"tid":5,"reid":0,"name":"娱乐","logo":"http://i0.hdslb.com/bfs/archive/31fa219e86c646a45a3a054609892de1e7071f97.png","goto":"","param":"","children":[{"tid":71,"reid":5,"name":"综艺","logo":"","goto":"","param":""},{"tid":137,"reid":5,"name":"明星","logo":"","goto":"","param":""},{"tid":131,"reid":5,"name":"Korea相关","logo":"","goto":"","param":""}]},{"tid":23,"reid":0,"name":"电影","logo":"","goto":"","param":"","children":[{"tid":82,"reid":23,"name":"电影相关","logo":"","goto":"","param":""},{"tid":85,"reid":23,"name":"短片","logo":"","goto":"","param":""},{"tid":145,"reid":23,"name":"欧美电影","logo":"","goto":"","param":""},{"tid":146,"reid":23,"name":"日本电影","logo":"","goto":"","param":""},{"tid":147,"reid":23,"name":"国产电影","logo":"","goto":"","param":""},{"tid":83,"reid":23,"name":"其他国家","logo":"","goto":"","param":""}]},{"tid":11,"reid":0,"name":"电视剧","logo":"","goto":"","param":"","children":[{"tid":15,"reid":11,"name":"连载剧集","logo":"","goto":"","param":""},{"tid":34,"reid":11,"name":"完结剧集","logo":"","goto":"","param":""},{"tid":86,"reid":11,"name":"特摄·布袋戏","logo":"","goto":"","param":""},{"tid":128,"reid":11,"name":"电视剧相关","logo":"","goto":"","param":""}]},{"tid":65539,"reid":0,"name":"游戏中心","logo":"http://i0.hdslb.com/bfs/archive/656df3124c81dd0e19bdc0a3e017091268b3db73.jpg","goto":"","param":""}]
   * message : ok
   * ver : 2223791243521558429
   */

  private int code;

  private String message;

  private String ver;

  /**
   * tid : 13
   * reid : 0
   * name : 番剧
   * logo :
   * goto :
   * param :
   * children : [{"tid":33,"reid":13,"name":"连载动画","logo":"http://i0.hdslb.com/u_user/54f589cd0573f9ab5c735698ae156d19.png","goto":"","param":""},{"tid":32,"reid":13,"name":"完结动画","logo":"http://i0.hdslb.com/u_user/18ad593e4b2f90b233f817e028bee71d.png","goto":"","param":""},{"tid":153,"reid":13,"name":"国产动画","logo":"http://i0.hdslb.com/u_user/405774aed11d0538a3548109a598fd80.png","goto":"","param":""},{"tid":51,"reid":13,"name":"资讯","logo":"","goto":"","param":""},{"tid":152,"reid":13,"name":"官方延伸","logo":"http://i0.hdslb.com/u_user/a78fa47e8f25772d51db1a19fe8b310f.png","goto":"","param":""}]
   */

  private List<DataBean> data;


  public int getCode() {

    return code;
  }


  public void setCode(int code) {

    this.code = code;
  }


  public String getMessage() {

    return message;
  }


  public void setMessage(String message) {

    this.message = message;
  }


  public String getVer() {

    return ver;
  }


  public void setVer(String ver) {

    this.ver = ver;
  }


  public List<DataBean> getData() {

    return data;
  }


  public void setData(List<DataBean> data) {

    this.data = data;
  }


  public static class DataBean implements Parcelable {

    private int tid;

    private int reid;

    private String name;

    private String logo;

    @SerializedName("goto")
    private String gotoX;

    private String param;

    /**
     * tid : 33
     * reid : 13
     * name : 连载动画
     * logo : http://i0.hdslb.com/u_user/54f589cd0573f9ab5c735698ae156d19.png
     * goto :
     * param :
     */

    private List<ChildrenBean> children;


    public int getTid() {

      return tid;
    }


    public void setTid(int tid) {

      this.tid = tid;
    }


    public int getReid() {

      return reid;
    }


    public void setReid(int reid) {

      this.reid = reid;
    }


    public String getName() {

      return name;
    }


    public void setName(String name) {

      this.name = name;
    }


    public String getLogo() {

      return logo;
    }


    public void setLogo(String logo) {

      this.logo = logo;
    }


    public String getGotoX() {

      return gotoX;
    }


    public void setGotoX(String gotoX) {

      this.gotoX = gotoX;
    }


    public String getParam() {

      return param;
    }


    public void setParam(String param) {

      this.param = param;
    }


    public List<ChildrenBean> getChildren() {

      return children;
    }


    public void setChildren(List<ChildrenBean> children) {

      this.children = children;
    }


    public static class ChildrenBean implements Parcelable {

      private int tid;

      private int reid;

      private String name;

      private String logo;

      @SerializedName("goto")
      private String gotoX;

      private String param;


      public int getTid() {

        return tid;
      }


      public void setTid(int tid) {

        this.tid = tid;
      }


      public int getReid() {

        return reid;
      }


      public void setReid(int reid) {

        this.reid = reid;
      }


      public String getName() {

        return name;
      }


      public void setName(String name) {

        this.name = name;
      }


      public String getLogo() {

        return logo;
      }


      public void setLogo(String logo) {

        this.logo = logo;
      }


      public String getGotoX() {

        return gotoX;
      }


      public void setGotoX(String gotoX) {

        this.gotoX = gotoX;
      }


      public String getParam() {

        return param;
      }


      public void setParam(String param) {

        this.param = param;
      }


      @Override
      public int describeContents() {

        return 0;
      }


      @Override
      public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.tid);
        dest.writeInt(this.reid);
        dest.writeString(this.name);
        dest.writeString(this.logo);
        dest.writeString(this.gotoX);
        dest.writeString(this.param);
      }


      public ChildrenBean() {

      }


      protected ChildrenBean(Parcel in) {

        this.tid = in.readInt();
        this.reid = in.readInt();
        this.name = in.readString();
        this.logo = in.readString();
        this.gotoX = in.readString();
        this.param = in.readString();
      }


      public static final Creator<ChildrenBean> CREATOR = new Creator<ChildrenBean>() {

        @Override
        public ChildrenBean createFromParcel(Parcel source) {

          return new ChildrenBean(source);
        }


        @Override
        public ChildrenBean[] newArray(int size) {

          return new ChildrenBean[size];
        }
      };
    }


    @Override
    public int describeContents() {

      return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

      dest.writeInt(this.tid);
      dest.writeInt(this.reid);
      dest.writeString(this.name);
      dest.writeString(this.logo);
      dest.writeString(this.gotoX);
      dest.writeString(this.param);
      dest.writeList(this.children);
    }


    public DataBean() {

    }


    protected DataBean(Parcel in) {

      this.tid = in.readInt();
      this.reid = in.readInt();
      this.name = in.readString();
      this.logo = in.readString();
      this.gotoX = in.readString();
      this.param = in.readString();
      this.children = new ArrayList<ChildrenBean>();
      in.readList(this.children, ChildrenBean.class.getClassLoader());
    }


    public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {

      @Override
      public DataBean createFromParcel(Parcel source) {

        return new DataBean(source);
      }


      @Override
      public DataBean[] newArray(int size) {

        return new DataBean[size];
      }
    };
  }


  @Override
  public int describeContents() {

    return 0;
  }


  @Override
  public void writeToParcel(Parcel dest, int flags) {

    dest.writeInt(this.code);
    dest.writeString(this.message);
    dest.writeString(this.ver);
    dest.writeList(this.data);
  }


  public RegionTypesInfo() {

  }


  protected RegionTypesInfo(Parcel in) {

    this.code = in.readInt();
    this.message = in.readString();
    this.ver = in.readString();
    this.data = new ArrayList<DataBean>();
    in.readList(this.data, DataBean.class.getClassLoader());
  }


  public static final Creator<RegionTypesInfo> CREATOR = new Creator<RegionTypesInfo>() {

    @Override
    public RegionTypesInfo createFromParcel(Parcel source) {

      return new RegionTypesInfo(source);
    }


    @Override
    public RegionTypesInfo[] newArray(int size) {

      return new RegionTypesInfo[size];
    }
  };
}
