
export default ({app, $axios, $mediator}) => {


  $axios.onRequest(config => {
    app.$eventHub.$emit('before-request',config);
    return config;
  });
  $axios.onRequestError(error => {
    app.$eventHub.$emit('request-error',error);
  })
  $axios.onResponse(response => {
    app.$eventHub.$emit('after-response',response);
    return response;
  })
  $axios.onRequestError(error => {
    app.$eventHub.$emit('response-error',error);
  })
}
