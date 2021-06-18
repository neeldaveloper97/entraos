# quadim-ai

## Build Setup

```bash
# install dependencies
$ npm install

# serve with hot reload at localhost:3000
$ npm run dev

# build for production and launch server
$ npm run build
$ npm run start

# generate static project
$ npm run generate
```

For detailed explanation on how things work, check out [Nuxt.js docs](https://nuxtjs.org).


# Coding Style

## Vuex vs Props 

Data that is used repeatedly throughout the page, down many DOM hierarchies, is the ideal case in which to use vuex, 
but there is a few scenarios which is an exception:

  ### you are dealing with forms and want to split the form in multiple components it is a bad idea to use vuex for these reasons:

   >What if your request fails ? You have to reset the form in vuex.
   
   >What if your object is used in multiple other places, and you empty a field? You might get unwanted behaviour. 
   
   >What if your vuex data might be changed by other component in the meanwhile?

  ### Solution?

> Mutate the parent data through 2 way binding. 
>
> We cannot mutate props directly, because if a component rerenders which can happen for many reasons, the prop will be overwritten. 
> so the solution is following:
> 
>  ####Parent Component
> 
>     <template>
>       <SelectConnectionsModal v-model="form.selectedConnections"/>
>     </template>
>     export default {
>        data() {
>            return {
>            form: {},
>            }
>        }
>     }
>
>    ####Child component
> 
>     //arrays
>     addConnection(payload) {
>     this.$emit('input', this.lodash.tap(this.lodash.cloneDeep(this.selected_connections), v => v.push(payload)))
>     },
>
>     removeConnection(index) {
>      this.$emit('input', this.lodash.tap(this.lodash.cloneDeep(this.selected_connections), v => v.splice(index, 1)))
>     },
> 
> 
>     //nested arrays
> 
>     addContact() {
>      this.$emit('input', this.lodash.tap(this.lodash.cloneDeep(this.selected_connections), v => v.contacts.push({
>        value: this.newContactValue,
>      })))
>     },
> 
>     removeContact(i) {
>      this.$emit('input', this.lodash.tap(this.lodash.cloneDeep(this.selected_connections), v => v.contacts.splice(i, 1)))
>      },
>     
> 
> 
>     replace(selected_connections_clone){
>      this.$emit('input', this.lodash.cloneDeep(selected_connections_clone));
>     },
> 
>     //or if you want to update an field
>      update(key, value) {
>      this.$emit('input', this.lodash.tap(this.lodash.cloneDeep(this.selected_connections), v => this.lodash.set(v, key, value)))
>     },
> 
>     clearValue(key) {
>      this.$emit('input', this.lodash.tap(this.lodash.cloneDeep(this.selected_connections), v => this.lodash.set(v, key, '')))
>     }
> 
>     //merging two forms together, where second parameter overwrites the first
>     merge(value){
>      this.$emit('input', this.lodash.cloneDeep({...this.formProp,...value}));
> 
   

