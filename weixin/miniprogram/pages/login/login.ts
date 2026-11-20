// 登录 / 注册页
Page({
  data: {
    // 当前模式：login 登录 / register 注册
    mode: 'login' as 'login' | 'register',
    // 登录表单
    form: {
      username: '',
      password: ''
    },
    // 注册表单
    registerForm: {
      username: '',
      password: '',
      nickname: '',
      phone: ''
    },
    // 是否提交中
    submitting: false
  },

  /**
   * 切换登录 / 注册模式
   */
  toggleMode() {
    this.setData({ mode: this.data.mode === 'login' ? 'register' : 'login' })
  },

  /**
   * 登录表单输入
   *
   * @param {object} event 事件对象
   */
  onLoginInput(event: any) {
    this.setData({ ['form.' + event.currentTarget.dataset.field]: event.detail.value })
  },

  /**
   * 注册表单输入
   *
   * @param {object} event 事件对象
   */
  onRegisterInput(event: any) {
    this.setData({ ['registerForm.' + event.currentTarget.dataset.field]: event.detail.value })
  },

  /**
   * 提交登录
   */
  submitLogin() {
    const { username, password } = this.data.form
    if (!username.trim()) {
      wx.showToast({ title: '请输入登录账号', icon: 'none' })
      return
    }
    if (!password.trim()) {
      wx.showToast({ title: '请输入登录密码', icon: 'none' })
      return
    }
    this.setData({ submitting: true })
    import('../../utils/request').then(({ post }) => {
      post('/auth/login', { username: username.trim(), password: password.trim() }).then((res: any) => {
        import('../../utils/auth').then(({ setLoginInfo }) => {
          setLoginInfo(res.data)
          wx.showToast({ title: '登录成功', icon: 'success' })
          setTimeout(() => {
            wx.navigateBack({ fail: () => { wx.switchTab({ url: '/pages/index/index' }) } })
          }, 800)
        })
      }).catch(() => {
        this.setData({ submitting: false })
      })
    })
  },

  /**
   * 提交注册
   */
  submitRegister() {
    const { username, password, nickname, phone } = this.data.registerForm
    if (!username.trim()) {
      wx.showToast({ title: '请输入登录账号', icon: 'none' })
      return
    }
    if (!password.trim()) {
      wx.showToast({ title: '请输入登录密码', icon: 'none' })
      return
    }
    this.setData({ submitting: true })
    import('../../utils/request').then(({ post }) => {
      post('/auth/register', {
        username: username.trim(),
        password: password.trim(),
        nickname: nickname.trim(),
        phone: phone.trim()
      }).then(() => {
        wx.showToast({ title: '注册成功，请登录', icon: 'success' })
        this.setData({
          submitting: false,
          mode: 'login',
          form: { username: username.trim(), password: password.trim() },
          registerForm: { username: '', password: '', nickname: '', phone: '' }
        })
      }).catch(() => {
        this.setData({ submitting: false })
      })
    })
  },

  /**
   * 微信一键登录（模拟授权，使用默认测试账号）
   */
  wxLogin() {
    wx.showToast({ title: '演示环境使用账号登录', icon: 'none' })
    this.setData({ form: { username: 'zhangsan', password: '123456' } })
  }
})
