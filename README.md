NineGridView：九宫格图片显示器
====
类似微信朋友圈九宫格图片
------

**2.0.0版本重构后与1.X版本不兼容，请谨慎升级**

### 使用方法:

**1.添加Gradle依赖：**
【最新版本号以[这里](https://github.com/Vanish136/NineGridView/releases)为准】

```
#last-verison请查看上面的最新版本号

dependencies{
         implementation 'com.lwkandroid.library:NineGridView:last-version'
    }
```
<br/>

**2.相关属性和方法：**

```
        #第一步 设置相关参数或属性        
        //设置图片分割间距，默认8dp，默认对应attr属性中divider_line_size
        mNineGridView.setDividerLineSize(TypedValue.COMPLEX_UNIT_PX,12);
        //设置是否开启编辑模式，默认false，对应attr属性中enable_edit_mode
        mNineGridView.setEnableEditMode(true);
        //设置水平方向上有多少列，默认3，对应attr属性中horizontal_child_count
        mNineGridView.setHorizontalChildCount(4);
        //设置“+”图片，对应attr属性中icon_plus_drawable
        mNineGridView.setIconPlusDrawable(R.drawable.ic_ngv_add_pic);
        //设置“x”图片，对应attr属性中icon_delete_drawable
        mNineGridView.setIconDeleteDrawable(R.drawable.ic_ngv_delete);
        //设置“x”图片尺寸与父容器尺寸的比例，默认0.2f，范围[0,1]，对应attr属性中icon_delete_size_ratio
        mNineGridView.setIconDeleteSizeRatio(0.15f);
        //设置非编辑模式下，只有一张图片时的尺寸，默认都为0，当宽高都非0才生效，且不会超过NineGridView内部可用总宽度，对应attr属性中single_image_width、single_image_height
        mNineGridView.setSingleImageSize(TypedValue.COMPLEX_UNIT_DIP,150,200);
  

        #第二步，设置适配器
        //创建图片加载器
        private static class GlideDisplayer2 implements INgvImageLoader<String>
        {
            @Override
            public void load(String source, ImageView imageView, int width, int height)
            {
                Glide.with(imageView.getContext())
                        .load(source)
                        .apply(new RequestOptions().override(width, height))
                        .into(imageView);
            }
        }

        //NineGridView的数据适配器，构造方法中必须设置最大数据容量和图片加载器
        DefaultNgvAdapter<String> ngvAdapter = new DefaultNgvAdapter<>(100, new GlideDisplayer2());
        //设置点击事件
        ngvAdapter.setOnChildClickListener(new DefaultNgvAdapter.OnChildClickedListener<String>()
        {

            @Override
            public void onPlusImageClicked(ImageView plusImageView, int dValueToLimited)
            {
               //点击“+”号图片后的回调
               //plusImageView代表“+”号图片对象，dValueToLimited代表当前可继续添加的图片数量
            }

            @Override
            public void onContentImageClicked(int position, ImageBean data, NgvChildImageView childImageView)
            {
                //点击图片时的回调
            }

            @Override
            public void onImageDeleted(int position, ImageBean data)
            {
                //点击删除按钮后的回调
                                //内部执行完删除后才回调，开发者无需处理删除逻辑
            }
          
        });

         #第三步，关联适配器
         mNineGridView.setAdapter(ngvAdapter);
```
<br/>

### 效果图:
![](https://github.com/Vanish136/NineGridView/raw/master/screenshoot/sample1.jpg) <br />
![](https://github.com/Vanish136/NineGridView/raw/master/screenshoot/sample2.jpg)

### 混淆配置：
无需额外混淆配置
