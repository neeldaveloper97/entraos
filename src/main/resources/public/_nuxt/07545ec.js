(window.webpackJsonp=window.webpackJsonp||[]).push([[3],{308:function(t,e,n){"use strict";n.r(e);n(2),n(3),n(4),n(5);var o={name:"Login",auth:!1,mounted:function(){var t=this.$route.query.access_token;this.$auth.ctx.app.$axios.setHeader("Authorization","Bearer "+t),this.$router.push("/ui")},methods:{}},r=n(19),component=Object(r.a)(o,(function(){var t=this.$createElement;return(this._self._c||t)("p",[this._v("Logging in...")])}),[],!1,null,"63ac8a7c",null);e.default=component.exports}}]);