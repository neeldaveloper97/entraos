import Vue from 'vue'
import VueLodash from 'vue-lodash'
import lodash from 'lodash'
Vue.use(VueLodash, { name: '_lodash' ,  lodash })
export default (ctx, inject) => {
  inject('lodash',lodash)
}
