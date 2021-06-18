
import Vue from 'vue'
Vue.filter('uppercase', function (value) {
  return value.toUpperCase()
})
Vue.filter('capitalize', function (value) {
  if (!value) return ''
  value = value.toString()
  return value.charAt(0).toUpperCase() + value.slice(1)
})
Vue.filter('titleize', function (value) {
  return value.replace(/(?:^|\s|-)\S/g, x => x.toUpperCase());
})
