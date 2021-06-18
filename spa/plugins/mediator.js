

export function createMediator(app) {
  return {
    send: (payload) => {
        if (payload.className !== null && payload.className.length > 0)
          app.$eventHub.$emit(payload.className,payload);
        else
        {
          console.error("Event doesnt have a className Identifier");
          throw new Error("Event doesnt have a className field Identifier");
        }
    },
    unsubscribe(event,payload){
      app.$eventHub.$off(event.className,payload);
    },
    subscribe(event,callback){
      app.$eventHub.$on(event.className, (payload) => {
        callback(payload);
      });
    },
    publish: async (payload) => {
      if (payload.className !== null && payload.className.length > 0)
      await app.$eventHub.$emit(payload.className,payload);
      else
      {
        console.error("Event doesnt have a className Identifier");
        throw new Error("Event doesnt have a className field Identifier");
      }
    }
  }
}
export default ({app}, inject) => {
  inject('mediator', createMediator(app))
}
