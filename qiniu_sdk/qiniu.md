# 集成传统直播

环信直播聊天室（以下称环信聊天室）中**传统直播**采用的是七牛直播SDK，集成了直播推流SDK和播放器SDK，展示了视频直播与直播聊天室结合的场景。</br>

## 集成七牛直播SDK步骤
>(1)注册七牛账号，并通过七牛官网申请并开通直播权限。</br>
>(2)从七牛直播云获取到直播推流SDK和播放器SDK，将jar包拷贝到libs目录下，so文件拷贝到jniLibs目录下。</br>
>(3)配置相关权限。如网络权限等。</br>
>(4)初始化SDK。</br>
>(5)修改布局，将SDK提供的推流及拉流View加入到布局中。环信聊天室中分别对这些View进行了继承，以便根据项目要求，做适当的修改。</br>
>(6)在LiveAnchorActivity添加推流逻辑，在LiveAudienceActivity中添加拉流逻辑。</br>

为了便于使用，环信聊天室中将七牛直播云的相关jar包，so文件及相关逻辑放到了qiniu_sdk library中，方便拆解及使用。</br>
qiuniu_sdk中PushStreamHelper作为推流的帮助类，提供了初始化及设置等逻辑，在LiveAnchorActivity通过PushStreamHelper进行使用。EncodingConfig为推流的配置类，用户可以根据自己项目的具体要求对默认配置进行修改或者设置。LiveCameraView为本地视频展示View，继承自SDK提供的GLSurfaceView类。LiveVideoView为视频播放类，继承自SDK提供的PLVideoTextureView，基本的设置逻辑也放在这个类中。</br>

## **注意事项：**

>(1)需注册七牛账号，并通过七牛官网申请并开通直播权限。</br>
>(2)SDK v3.0.0 及以后版本需要获取授权才可使用，即SDK会校验包名，需联系七牛客服获取授权。</br>
>(3)环信聊天室中推拉流地址需替换为自己的域名。</br>