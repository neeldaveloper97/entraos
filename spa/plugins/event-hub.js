import Vue from 'vue'

export default (ctx, inject) => {
  inject('eventHub', new Vue())
};
