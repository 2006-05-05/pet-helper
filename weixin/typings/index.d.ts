/**
 * 小程序全局类型声明入口
 *
 * <p>引入微信小程序官方 API 类型定义（miniprogram-api-typings）。</p>
 */
/// <reference types="miniprogram-api-typings" />

/**
 * 小程序全局数据配置
 *
 * <p>对应 App() 中的选项内容，会被合并到 App 实例上。</p>
 */
interface IAppOption {
  /** 全局数据 */
  globalData: {
    /** 后端服务地址 */
    baseUrl: string;
    /** 当前登录用户信息 */
    userInfo: any;
  };
  /**
   * 检查用户是否已登录
   *
   * @returns {boolean} 是否已登录
   */
  checkLogin(): boolean;
}
