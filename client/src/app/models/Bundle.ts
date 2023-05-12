export class Bundle{
  constructor(
    public bundleId: string,
		public date: string,
		public title: string,
		public name: string,
		public comments: string,
		public urls: string[]
  ){}
}
