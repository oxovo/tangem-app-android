// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: dex.proto

package com.tangem.blockchain.binance.proto;

/**
 * <pre>
 * please note the field name is the JSON name.
 * </pre>
 *
 * Protobuf type {@code transaction.TokenFreeze}
 */
public  final class TokenFreeze extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:transaction.TokenFreeze)
    TokenFreezeOrBuilder {
private static final long serialVersionUID = 0L;
  // Use TokenFreeze.newBuilder() to construct.
  private TokenFreeze(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private TokenFreeze() {
    from_ = com.google.protobuf.ByteString.EMPTY;
    symbol_ = "";
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private TokenFreeze(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {

            from_ = input.readBytes();
            break;
          }
          case 18: {
            String s = input.readStringRequireUtf8();

            symbol_ = s;
            break;
          }
          case 24: {

            amount_ = input.readInt64();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.tangem.blockchain.binance.proto.Transaction.internal_static_transaction_TokenFreeze_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.tangem.blockchain.binance.proto.Transaction.internal_static_transaction_TokenFreeze_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.tangem.blockchain.binance.proto.TokenFreeze.class, com.tangem.blockchain.binance.proto.TokenFreeze.Builder.class);
  }

  public static final int FROM_FIELD_NUMBER = 1;
  private com.google.protobuf.ByteString from_;
  /**
   * <pre>
   *    0xE774B32D   // hardcoded, object type prefix in 4 bytes
   * </pre>
   *
   * <code>bytes from = 1;</code>
   */
  public com.google.protobuf.ByteString getFrom() {
    return from_;
  }

  public static final int SYMBOL_FIELD_NUMBER = 2;
  private volatile Object symbol_;
  /**
   * <pre>
   * token symbol, in full name with "-" suffix
   * </pre>
   *
   * <code>string symbol = 2;</code>
   */
  public String getSymbol() {
    Object ref = symbol_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      symbol_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * token symbol, in full name with "-" suffix
   * </pre>
   *
   * <code>string symbol = 2;</code>
   */
  public com.google.protobuf.ByteString
      getSymbolBytes() {
    Object ref = symbol_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      symbol_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int AMOUNT_FIELD_NUMBER = 3;
  private long amount_;
  /**
   * <pre>
   * amount of token to freeze
   * </pre>
   *
   * <code>int64 amount = 3;</code>
   */
  public long getAmount() {
    return amount_;
  }

  private byte memoizedIsInitialized = -1;
  @Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!from_.isEmpty()) {
      output.writeBytes(1, from_);
    }
    if (!getSymbolBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, symbol_);
    }
    if (amount_ != 0L) {
      output.writeInt64(3, amount_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!from_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(1, from_);
    }
    if (!getSymbolBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, symbol_);
    }
    if (amount_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(3, amount_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.tangem.blockchain.binance.proto.TokenFreeze)) {
      return super.equals(obj);
    }
    com.tangem.blockchain.binance.proto.TokenFreeze other = (com.tangem.blockchain.binance.proto.TokenFreeze) obj;

    if (!getFrom()
        .equals(other.getFrom())) return false;
    if (!getSymbol()
        .equals(other.getSymbol())) return false;
    if (getAmount()
        != other.getAmount()) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + FROM_FIELD_NUMBER;
    hash = (53 * hash) + getFrom().hashCode();
    hash = (37 * hash) + SYMBOL_FIELD_NUMBER;
    hash = (53 * hash) + getSymbol().hashCode();
    hash = (37 * hash) + AMOUNT_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getAmount());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.tangem.blockchain.binance.proto.TokenFreeze parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.tangem.blockchain.binance.proto.TokenFreeze parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.tangem.blockchain.binance.proto.TokenFreeze prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @Override
  protected Builder newBuilderForType(
      BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * please note the field name is the JSON name.
   * </pre>
   *
   * Protobuf type {@code transaction.TokenFreeze}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:transaction.TokenFreeze)
      com.tangem.blockchain.binance.proto.TokenFreezeOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.tangem.blockchain.binance.proto.Transaction.internal_static_transaction_TokenFreeze_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.tangem.blockchain.binance.proto.Transaction.internal_static_transaction_TokenFreeze_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.tangem.blockchain.binance.proto.TokenFreeze.class, com.tangem.blockchain.binance.proto.TokenFreeze.Builder.class);
    }

    // Construct using com.tangem.blockchain.binance.proto.TokenFreeze.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @Override
    public Builder clear() {
      super.clear();
      from_ = com.google.protobuf.ByteString.EMPTY;

      symbol_ = "";

      amount_ = 0L;

      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.tangem.blockchain.binance.proto.Transaction.internal_static_transaction_TokenFreeze_descriptor;
    }

    @Override
    public com.tangem.blockchain.binance.proto.TokenFreeze getDefaultInstanceForType() {
      return com.tangem.blockchain.binance.proto.TokenFreeze.getDefaultInstance();
    }

    @Override
    public com.tangem.blockchain.binance.proto.TokenFreeze build() {
      com.tangem.blockchain.binance.proto.TokenFreeze result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public com.tangem.blockchain.binance.proto.TokenFreeze buildPartial() {
      com.tangem.blockchain.binance.proto.TokenFreeze result = new com.tangem.blockchain.binance.proto.TokenFreeze(this);
      result.from_ = from_;
      result.symbol_ = symbol_;
      result.amount_ = amount_;
      onBuilt();
      return result;
    }

    @Override
    public Builder clone() {
      return super.clone();
    }
    @Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    @Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    @Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.tangem.blockchain.binance.proto.TokenFreeze) {
        return mergeFrom((com.tangem.blockchain.binance.proto.TokenFreeze)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.tangem.blockchain.binance.proto.TokenFreeze other) {
      if (other == com.tangem.blockchain.binance.proto.TokenFreeze.getDefaultInstance()) return this;
      if (other.getFrom() != com.google.protobuf.ByteString.EMPTY) {
        setFrom(other.getFrom());
      }
      if (!other.getSymbol().isEmpty()) {
        symbol_ = other.symbol_;
        onChanged();
      }
      if (other.getAmount() != 0L) {
        setAmount(other.getAmount());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @Override
    public final boolean isInitialized() {
      return true;
    }

    @Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.tangem.blockchain.binance.proto.TokenFreeze parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.tangem.blockchain.binance.proto.TokenFreeze) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private com.google.protobuf.ByteString from_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <pre>
     *    0xE774B32D   // hardcoded, object type prefix in 4 bytes
     * </pre>
     *
     * <code>bytes from = 1;</code>
     */
    public com.google.protobuf.ByteString getFrom() {
      return from_;
    }
    /**
     * <pre>
     *    0xE774B32D   // hardcoded, object type prefix in 4 bytes
     * </pre>
     *
     * <code>bytes from = 1;</code>
     */
    public Builder setFrom(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }

      from_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     *    0xE774B32D   // hardcoded, object type prefix in 4 bytes
     * </pre>
     *
     * <code>bytes from = 1;</code>
     */
    public Builder clearFrom() {

      from_ = getDefaultInstance().getFrom();
      onChanged();
      return this;
    }

    private Object symbol_ = "";
    /**
     * <pre>
     * token symbol, in full name with "-" suffix
     * </pre>
     *
     * <code>string symbol = 2;</code>
     */
    public String getSymbol() {
      Object ref = symbol_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        symbol_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <pre>
     * token symbol, in full name with "-" suffix
     * </pre>
     *
     * <code>string symbol = 2;</code>
     */
    public com.google.protobuf.ByteString
        getSymbolBytes() {
      Object ref = symbol_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        symbol_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * token symbol, in full name with "-" suffix
     * </pre>
     *
     * <code>string symbol = 2;</code>
     */
    public Builder setSymbol(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }

      symbol_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * token symbol, in full name with "-" suffix
     * </pre>
     *
     * <code>string symbol = 2;</code>
     */
    public Builder clearSymbol() {

      symbol_ = getDefaultInstance().getSymbol();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * token symbol, in full name with "-" suffix
     * </pre>
     *
     * <code>string symbol = 2;</code>
     */
    public Builder setSymbolBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

      symbol_ = value;
      onChanged();
      return this;
    }

    private long amount_ ;
    /**
     * <pre>
     * amount of token to freeze
     * </pre>
     *
     * <code>int64 amount = 3;</code>
     */
    public long getAmount() {
      return amount_;
    }
    /**
     * <pre>
     * amount of token to freeze
     * </pre>
     *
     * <code>int64 amount = 3;</code>
     */
    public Builder setAmount(long value) {

      amount_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * amount of token to freeze
     * </pre>
     *
     * <code>int64 amount = 3;</code>
     */
    public Builder clearAmount() {

      amount_ = 0L;
      onChanged();
      return this;
    }
    @Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:transaction.TokenFreeze)
  }

  // @@protoc_insertion_point(class_scope:transaction.TokenFreeze)
  private static final com.tangem.blockchain.binance.proto.TokenFreeze DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.tangem.blockchain.binance.proto.TokenFreeze();
  }

  public static com.tangem.blockchain.binance.proto.TokenFreeze getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<TokenFreeze>
      PARSER = new com.google.protobuf.AbstractParser<TokenFreeze>() {
    @Override
    public TokenFreeze parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new TokenFreeze(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<TokenFreeze> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<TokenFreeze> getParserForType() {
    return PARSER;
  }

  @Override
  public com.tangem.blockchain.binance.proto.TokenFreeze getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

