/*global window*/

const MAX_ID = 899999;

export class MultiVariantTest {
  constructor(name, audience, audienceOffset, isServerSide, variants) {
    this.name = name;
    this.audience = audience;
    this.audienceOffset = audienceOffset;
    this.isServerSide = isServerSide;
    this.variants = variants;
  }


  isInTest(mvtId, maxId = MAX_ID) {
    const minBound = maxId * this.audienceOffset,
      maxBound = minBound + maxId * this.audience;

    return minBound < mvtId && mvtId <= maxBound
  }

  activeVariant(mvtId, maxId = MAX_ID) {
    if (this.isInTest(mvtId, maxId)) {
      return this.variants[mvtId % this.variants.length];
    }

    return undefined;
  }


  static fromObject(obj) {
    const variants = obj.variants.map(v => v.id);
    return new MultiVariantTest(obj.name, obj.audience, obj.audienceOffset, obj.isServerSide, variants)
  }

  static initFromPageConfig() {
    if (window._idConfig && Array.isArray(window._idConfig.mvtTests)) {
      return window._idConfig.mvtTests.map(test => MultiVariantTest.fromObject(test));
    }

    return [];
  }
}
