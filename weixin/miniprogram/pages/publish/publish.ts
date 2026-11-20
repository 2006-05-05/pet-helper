// 发布领养信息页
Page({
  data: {
    // 分类列表
    categories: [] as any[],
    // 性别选项
    genders: ['弟弟', '妹妹', '未知'],
    // 当前表单
    form: {
      name: '',
      categoryId: 0,
      age: '',
      gender: '未知',
      city: '',
      price: 0,
      health: '',
      description: '',
      vaccine: 0,
      sterilized: 0,
      deworm: 0
    },
    // 已选择的图片
    images: [] as string[],
    // 是否提交中
    submitting: false,
    // 是否已登录
    hasToken: false
  },

  /**
   * 页面加载
   */
  onLoad() {
    this.loadCategories()
    // 回填当前用户所在城市
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo && userInfo.city) {
      this.setData({ 'form.city': userInfo.city })
    }
  },

  /**
   * 页面显示时检查登录状态
   */
  onShow() {
    this.setData({ hasToken: !!wx.getStorageSync('token') })
  },

  /**
   * 跳转登录页
   */
  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  /**
   * 加载分类
   */
  loadCategories() {
    import('../../utils/request').then(({ get }) => {
      get('/category/list').then((res: any) => {
        const list = res.data || []
        this.setData({
          categories: list,
          'form.categoryId': list.length ? list[0].id : 0
        })
      })
    })
  },

  /**
   * 表单输入
   *
   * @param {object} event 事件对象
   */
  onInput(event: any) {
    this.setData({ ['form.' + event.currentTarget.dataset.field]: event.detail.value })
  },

  /**
   * 选择分类
   *
   * @param {object} event 事件对象
   */
  selectCategory(event: any) {
    this.setData({ 'form.categoryId': event.currentTarget.dataset.id })
  },

  /**
   * 选择性别
   *
   * @param {object} event 事件对象
   */
  selectGender(event: any) {
    this.setData({ 'form.gender': event.currentTarget.dataset.value })
  },

  /**
   * 切换防疫选项
   *
   * @param {object} event 事件对象
   */
  toggleFlag(event: any) {
    const field = event.currentTarget.dataset.field as 'vaccine' | 'sterilized' | 'deworm'
    const current = this.data.form[field]
    this.setData({ ['form.' + field]: current === 1 ? 0 : 1 })
  },

  /**
   * 选择图片
   */
  chooseImage() {
    wx.chooseMedia({
      count: 5 - this.data.images.length,
      mediaType: ['image'],
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const paths = res.tempFiles.map((file: any) => file.tempFilePath)
        this.setData({ images: this.data.images.concat(paths) })
      }
    })
  },

  /**
   * 移除图片
   *
   * @param {object} event 事件对象
   */
  removeImage(event: any) {
    const list = this.data.images.slice()
    list.splice(event.currentTarget.dataset.index, 1)
    this.setData({ images: list })
  },

  /**
   * 预览图片
   *
   * @param {object} event 事件对象
   */
  previewImage(event: any) {
    wx.previewImage({
      current: event.currentTarget.dataset.src,
      urls: this.data.images
    })
  },

  /**
   * 提交发布
   */
  submit() {
    const app = getApp()
    if (!app.checkLogin()) {
      return
    }
    const { name, categoryId, age, city, description } = this.data.form
    if (!name.trim()) {
      wx.showToast({ title: '请填写宠物昵称', icon: 'none' })
      return
    }
    if (!categoryId) {
      wx.showToast({ title: '请选择宠物分类', icon: 'none' })
      return
    }
    if (!city.trim()) {
      wx.showToast({ title: '请填写所在城市', icon: 'none' })
      return
    }
    if (!description.trim()) {
      wx.showToast({ title: '请填写宠物介绍', icon: 'none' })
      return
    }
    this.setData({ submitting: true })
    import('../../utils/request').then(({ post, uploadImage }) => {
      // 依次上传图片，收集地址
      const urls: string[] = []
      const uploadTasks: Promise<void>[] = this.data.images.map((path) =>
        uploadImage(path).then((url: string) => {
          urls.push(url)
        })
      )
      Promise.all(uploadTasks).then(() => {
        post('/pet', {
          name: name.trim(),
          categoryId: categoryId,
          age: age.trim(),
          gender: this.data.form.gender,
          city: city.trim(),
          price: Number(this.data.form.price) || 0,
          health: this.data.form.health.trim(),
          description: description.trim(),
          vaccine: this.data.form.vaccine,
          sterilized: this.data.form.sterilized,
          deworm: this.data.form.deworm,
          coverImg: urls.length ? urls[0] : '',
          images: urls
        }).then(() => {
          wx.showToast({ title: '发布成功', icon: 'success' })
          setTimeout(() => {
            wx.switchTab({ url: '/pages/index/index' })
          }, 900)
        }).catch(() => {
          this.setData({ submitting: false })
        })
      }).catch(() => {
        this.setData({ submitting: false })
      })
    })
  }
})
