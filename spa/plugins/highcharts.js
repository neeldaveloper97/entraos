import Vue from "vue";
import Highcharts from 'highcharts'
import highchartsMore from 'highcharts/highcharts-more'
import drag from 'highcharts/modules/draggable-points';
import gauge from 'highcharts/modules/solid-gauge';
import HighchartsVue from 'highcharts-vue'
if (typeof Highcharts === "object") {
  highchartsMore(Highcharts);
  drag(Highcharts);
  gauge(Highcharts);
  (function(H) {
    H.wrap(H.seriesTypes.packedbubble.prototype, 'onMouseDown', function(proceed, point, event) {
      proceed.apply(this, Array.prototype.slice.call(arguments, 1));
      point.importEvents();
      H.fireEvent(point, 'dragStart',event);
    });
    H.wrap(H.seriesTypes.packedbubble.prototype, 'onMouseMove', function(proceed, point, event) {
      proceed.apply(this, Array.prototype.slice.call(arguments, 1));
      if (point.fixedPosition && point.inDragMode) {
        H.fireEvent(point, 'drag',event);
      }
    });
    H.wrap(H.seriesTypes.packedbubble.prototype, 'onMouseUp', function(proceed, point, event) {
      if (point.fixedPosition && point.inDragMode) {
        H.fireEvent(point, 'drop',point);
      }
      proceed.apply(this, Array.prototype.slice.call(arguments, 1));
    });

  })(Highcharts);
}
Vue.use(HighchartsVue);
