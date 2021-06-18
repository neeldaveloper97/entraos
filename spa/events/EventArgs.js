// The base class for all event classes
export class EventArgs {
  constructor() {
    this.className = new Error().stack.split(/\r\n|\r|\n/g)[1].trim();
    console.log(this.className);
  }
}
