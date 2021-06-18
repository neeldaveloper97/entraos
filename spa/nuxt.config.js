export default {

  /*
  ** Nuxt rendering mode
  ** See https://nuxtjs.org/api/configuration-mode
  */
  ssr: true,

  /*
  ** Nuxt target
  ** See https://nuxtjs.org/api/configuration-target
  */
  target: 'static',

  /*
  ** Headers of the page
  ** See https://nuxtjs.org/api/configuration-head
  */
  head: {
    title: process.env.npm_package_name || '',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: process.env.npm_package_description || '' }
    ],
    link: [
      {
        rel: 'icon', type: 'image/x-icon',
        href: '/favicon.ico'
      }
    ],
    script: [{
      src: 'https://maps.googleapis.com/maps/api/js?key=AIzaSyDDDW6OIkPWoD3VheJbHAH-FGJQ0mn1TYU&libraries=places'
    }]
  },
  /*
  ** Global CSS
  */
  css: [
    '@/assets/styles/main.scss',
    // '@/assets/styles/bulma_modules.sass'
  ],
  /*
  ** Plugins to load before mounting the App
  ** https://nuxtjs.org/guide/plugins
  */

  plugins: ['~plugins/vuelidate.js',
    { src: '~/plugins/vue-slider.js', ssr: false },
    '~plugins/mediator.js',
    '~plugins/filters.js',
    "~plugins/unit-of-work.js",
    '~plugins/highcharts.js',
    '~plugins/lodash.js',
    '~plugins/axios.js',
    { src: "~plugins/click-outside.js", ssr: true},
    '~plugins/global.js',
    {src:'~/plugins/date-picker.js', mode: 'client'},
    {src : '~/plugins/vue-apexchart.js', ssr : false}

],
  //makes hub always loaded first, so other plugins can use it
  extendPlugins (plugins) {
    plugins.unshift('~/plugins/event-hub.js')
    return plugins
  },
  /*
  ** Auto import components
  ** See https://nuxtjs.org/api/configuration-components
  */
  components: false,
  /*
  ** Nuxt.js dev-modules
  */
  buildModules: [
    // Doc: https://github.com/nuxt-community/eslint-module
    '@nuxtjs/google-fonts',
    '@nuxtjs/eslint-module'
  ],
  googleFonts: {
    families: {
      'Noto+Sans+JP': [ 400,500,600, 700 ],
    },
    display: 'swap',
    prefetch:true
  },
  /*
  ** Nuxt.js modules
  */
  modules: [
    '@nuxtjs/vuetify',
    '@nuxtjs/toast',
    '~/modules/autoinjector.js',
    '@nuxtjs/auth-next',
    '@nuxtjs/sentry',
    'vue-scrollto/nuxt',
    '@nuxtjs/moment',
    // Doc: https://github.com/nuxt-community/modules/tree/master/packages/bulma
    // Doc: https://axios.nuxtjs.org/usage
    '@nuxtjs/axios',
    '@nuxtjs/style-resources',
    [
      'nuxt-fontawesome', {
      imports: [
        {
          set: '@fortawesome/free-solid-svg-icons',
          icons: ['faBars','faStarHalfAlt','faRulerHorizontal', 'faAngleRight','faExchangeAlt','faAngleLeft','faEnvelopeOpen','faEllipsisV','faAngleUp','faAngleDown','faUser','faChartBar','faTimes','faSearch','faHome','faPen','faSignOutAlt','faEnvelope','faAt','faPhone','faSignature','faCalendar','faPlus','faAddressBook','faBook','faUserAlt','faCircle','faGavel','faFile','faExclamationCircle']
        },
        {
          set:'@fortawesome/free-brands-svg-icons',
          icons: []
        }
      ]
    }
    ]
  ],
  toast: {
    position: 'bottom-center',
    fullWidth:true,
    duration:5000,
    closeOnSwipe:true,
    register: [ // Register custom toasts
      {
        name: 'my-error',
        message: 'Oops...Something went wrong',
        options: {
          type: 'error'
        }
      }
    ]
  },
  sentry: {
    dsn: 'https://f8cf946a403f4e709a6d6639a528da04@o450136.ingest.sentry.io/5434296', // Enter your project's DSN here
    config: {
      logErrors: true
    }, // Additional config
  },
  styleResources: {
    scss: ['~assets/styles/variables/_variables.scss','~assets/styles/_mixins.scss'],
  },
  /*
  ** Axios module configuration
  ** See https://axios.nuxtjs.org/options
  */
  axios: {
    baseURL: process.env.API_BASE_URL || 'https://contract-devtest.entraos.io/',
    proxyHeaders: false,
    credentials: false 
  },
  router: {
    middleware: ['auth']
  },
  auth: {
    redirect: {
      login: '/',
      callback: '/login',
      logout: '/',
      home: '/ui'
    },
    strategies: {
      social: {
        scheme: 'oauth2',
        endpoints: {
          authorization: `https://entrasso-devtest.entraos.io/oauth2/authorize`,
          token: 'https://entrasso-devtest.entraos.io/oauth2/token',
          userInfo: 'https://entrasso-devtest.entraos.io/oauth2/userinfo',
          logout: 'https://entrasso-devtest.entraos.io/oauth2/logout'
        },
        token: {
          property: 'access_token',
          type: 'Bearer',
          required: true,
          maxAge: 1800
        },
        refreshToken: {
          property: 'refresh_token',
          maxAge: 60 * 60 * 24 * 30
        },
        responseType: 'token',
        grantType: 'authorization_code',
        //accessType: undefined,
        //redirectUri: '/',
        //logoutRedirectUri: undefined,
        clientId: 'q8QBqvyVGuZI.S_wgFEJWzi9NbgJfxZ3swGMPGX.CwM-',
        scope: ['openid', 'profile', 'email'],
        state: 'UNIQUE_AND_NON_GUESSABLE',
        codeChallengeMethod: '',
        responseMode: '',
        acrValues: ''
      }
    }
    },
    /*
  ** Build configuration
  ** See https://nuxtjs.org/api/configuration-build/
  */
  build: {
    parallel: true,
    cache: true,
    analyze: false,
    optimizeCSS: process.env.NODE_ENV === 'production',
    postcss: {
      preset: {
        features: {
          customProperties: false
        }
      }
    },
    loaders:  {
      vue: {
        prettify: false
      }
    },
    extend(config, ctx) {
      config.module.rules.push({
        enforce: 'pre',
        test: /\.(js|vue)$/,
        loader: 'eslint-loader',
        exclude: /(node_modules)/,
        options: {
          fix: true,
          cache:true
        }
      })
    }
  }
}
