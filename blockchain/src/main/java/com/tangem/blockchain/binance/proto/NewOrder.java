// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: dex.proto

package com.tangem.blockchain.binance.proto;

/**
 * <pre>
 * please note the field name is the JSON name.
 * </pre>
 *
 * Protobuf type {@code transaction.NewOrder}
 */
public  final class NewOrder extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:transaction.NewOrder)
    NewOrderOrBuilder {
private static final long serialVersionUID = 0L;
  // Use NewOrder.newBuilder() to construct.
  private NewOrder(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private NewOrder() {
    sender_ = com.google.protobuf.ByteString.EMPTY;
    id_ = "";
    symbol_ = "";
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private NewOrder(
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

            sender_ = input.readBytes();
            break;
          }
          case 18: {
            String s = input.readStringRequireUtf8();

            id_ = s;
            break;
          }
          case 26: {
            String s = input.readStringRequireUtf8();

            symbol_ = s;
            break;
          }
          case 32: {

            ordertype_ = input.readInt64();
            break;
          }
          case 40: {

            side_ = input.readInt64();
            break;
          }
          case 48: {

            price_ = input.readInt64();
            break;
          }
          case 56: {

            quantity_ = input.readInt64();
            break;
          }
          case 64: {

            timeinforce_ = input.readInt64();
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
    return com.tangem.blockchain.binance.proto.Transaction.internal_static_transaction_NewOrder_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.tangem.blockchain.binance.proto.Transaction.internal_static_transaction_NewOrder_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.tangem.blockchain.binance.proto.NewOrder.class, com.tangem.blockchain.binance.proto.NewOrder.Builder.class);
  }

  public static final int SENDER_FIELD_NUMBER = 1;
  private com.google.protobuf.ByteString sender_;
  /**
   * <pre>
   *    0xCE6DC043 // hardcoded, object type prefix in 4 bytes
   * </pre>
   *
   * <code>bytes sender = 1;</code>
   */
  public com.google.protobuf.ByteString getSender() {
    return sender_;
  }

  public static final int ID_FIELD_NUMBER = 2;
  private volatile Object id_;
  /**
   * <pre>
   * order id, optional
   * </pre>
   *
   * <code>string id = 2;</code>
   */
  public String getId() {
    Object ref = id_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      id_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * order id, optional
   * </pre>
   *
   * <code>string id = 2;</code>
   */
  public com.google.protobuf.ByteString
      getIdBytes() {
    Object ref = id_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      id_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int SYMBOL_FIELD_NUMBER = 3;
  private volatile Object symbol_;
  /**
   * <pre>
   * symbol for trading pair in full name of the tokens
   * </pre>
   *
   * <code>string symbol = 3;</code>
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
   * symbol for trading pair in full name of the tokens
   * </pre>
   *
   * <code>string symbol = 3;</code>
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

  public static final int ORDERTYPE_FIELD_NUMBER = 4;
  private long ordertype_;
  /**
   * <pre>
   * only accept 2 for now, meaning limit order
   * </pre>
   *
   * <code>int64 ordertype = 4;</code>
   */
  public long getOrdertype() {
    return ordertype_;
  }

  public static final int SIDE_FIELD_NUMBER = 5;
  private long side_;
  /**
   * <pre>
   * 1 for buy and 2 fory sell
   * </pre>
   *
   * <code>int64 side = 5;</code>
   */
  public long getSide() {
    return side_;
  }

  public static final int PRICE_FIELD_NUMBER = 6;
  private long price_;
  /**
   * <pre>
   * price of the order, which is the real price multiplied by 1e8 (10^8) and rounded to integer
   * </pre>
   *
   * <code>int64 price = 6;</code>
   */
  public long getPrice() {
    return price_;
  }

  public static final int QUANTITY_FIELD_NUMBER = 7;
  private long quantity_;
  /**
   * <pre>
   * quantity of the order, which is the real price multiplied by 1e8 (10^8) and rounded to integer
   * </pre>
   *
   * <code>int64 quantity = 7;</code>
   */
  public long getQuantity() {
    return quantity_;
  }

  public static final int TIMEINFORCE_FIELD_NUMBER = 8;
  private long timeinforce_;
  /**
   * <pre>
   * 1 for Good Till Expire(GTE) order and 3 for Immediate Or Cancel (IOC)
   * </pre>
   *
   * <code>int64 timeinforce = 8;</code>
   */
  public long getTimeinforce() {
    return timeinforce_;
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
    if (!sender_.isEmpty()) {
      output.writeBytes(1, sender_);
    }
    if (!getIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, id_);
    }
    if (!getSymbolBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, symbol_);
    }
    if (ordertype_ != 0L) {
      output.writeInt64(4, ordertype_);
    }
    if (side_ != 0L) {
      output.writeInt64(5, side_);
    }
    if (price_ != 0L) {
      output.writeInt64(6, price_);
    }
    if (quantity_ != 0L) {
      output.writeInt64(7, quantity_);
    }
    if (timeinforce_ != 0L) {
      output.writeInt64(8, timeinforce_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!sender_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(1, sender_);
    }
    if (!getIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, id_);
    }
    if (!getSymbolBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, symbol_);
    }
    if (ordertype_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(4, ordertype_);
    }
    if (side_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(5, side_);
    }
    if (price_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(6, price_);
    }
    if (quantity_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(7, quantity_);
    }
    if (timeinforce_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(8, timeinforce_);
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
    if (!(obj instanceof com.tangem.blockchain.binance.proto.NewOrder)) {
      return super.equals(obj);
    }
    com.tangem.blockchain.binance.proto.NewOrder other = (com.tangem.blockchain.binance.proto.NewOrder) obj;

    if (!getSender()
        .equals(other.getSender())) return false;
    if (!getId()
        .equals(other.getId())) return false;
    if (!getSymbol()
        .equals(other.getSymbol())) return false;
    if (getOrdertype()
        != other.getOrdertype()) return false;
    if (getSide()
        != other.getSide()) return false;
    if (getPrice()
        != other.getPrice()) return false;
    if (getQuantity()
        != other.getQuantity()) return false;
    if (getTimeinforce()
        != other.getTimeinforce()) return false;
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
    hash = (37 * hash) + SENDER_FIELD_NUMBER;
    hash = (53 * hash) + getSender().hashCode();
    hash = (37 * hash) + ID_FIELD_NUMBER;
    hash = (53 * hash) + getId().hashCode();
    hash = (37 * hash) + SYMBOL_FIELD_NUMBER;
    hash = (53 * hash) + getSymbol().hashCode();
    hash = (37 * hash) + ORDERTYPE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getOrdertype());
    hash = (37 * hash) + SIDE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getSide());
    hash = (37 * hash) + PRICE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getPrice());
    hash = (37 * hash) + QUANTITY_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getQuantity());
    hash = (37 * hash) + TIMEINFORCE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getTimeinforce());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.tangem.blockchain.binance.proto.NewOrder parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.tangem.blockchain.binance.proto.NewOrder parseFrom(
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
  public static Builder newBuilder(com.tangem.blockchain.binance.proto.NewOrder prototype) {
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
   * Protobuf type {@code transaction.NewOrder}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:transaction.NewOrder)
      com.tangem.blockchain.binance.proto.NewOrderOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.tangem.blockchain.binance.proto.Transaction.internal_static_transaction_NewOrder_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.tangem.blockchain.binance.proto.Transaction.internal_static_transaction_NewOrder_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.tangem.blockchain.binance.proto.NewOrder.class, com.tangem.blockchain.binance.proto.NewOrder.Builder.class);
    }

    // Construct using com.tangem.blockchain.binance.proto.NewOrder.newBuilder()
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
      sender_ = com.google.protobuf.ByteString.EMPTY;

      id_ = "";

      symbol_ = "";

      ordertype_ = 0L;

      side_ = 0L;

      price_ = 0L;

      quantity_ = 0L;

      timeinforce_ = 0L;

      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.tangem.blockchain.binance.proto.Transaction.internal_static_transaction_NewOrder_descriptor;
    }

    @Override
    public com.tangem.blockchain.binance.proto.NewOrder getDefaultInstanceForType() {
      return com.tangem.blockchain.binance.proto.NewOrder.getDefaultInstance();
    }

    @Override
    public com.tangem.blockchain.binance.proto.NewOrder build() {
      com.tangem.blockchain.binance.proto.NewOrder result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public com.tangem.blockchain.binance.proto.NewOrder buildPartial() {
      com.tangem.blockchain.binance.proto.NewOrder result = new com.tangem.blockchain.binance.proto.NewOrder(this);
      result.sender_ = sender_;
      result.id_ = id_;
      result.symbol_ = symbol_;
      result.ordertype_ = ordertype_;
      result.side_ = side_;
      result.price_ = price_;
      result.quantity_ = quantity_;
      result.timeinforce_ = timeinforce_;
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
      if (other instanceof com.tangem.blockchain.binance.proto.NewOrder) {
        return mergeFrom((com.tangem.blockchain.binance.proto.NewOrder)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.tangem.blockchain.binance.proto.NewOrder other) {
      if (other == com.tangem.blockchain.binance.proto.NewOrder.getDefaultInstance()) return this;
      if (other.getSender() != com.google.protobuf.ByteString.EMPTY) {
        setSender(other.getSender());
      }
      if (!other.getId().isEmpty()) {
        id_ = other.id_;
        onChanged();
      }
      if (!other.getSymbol().isEmpty()) {
        symbol_ = other.symbol_;
        onChanged();
      }
      if (other.getOrdertype() != 0L) {
        setOrdertype(other.getOrdertype());
      }
      if (other.getSide() != 0L) {
        setSide(other.getSide());
      }
      if (other.getPrice() != 0L) {
        setPrice(other.getPrice());
      }
      if (other.getQuantity() != 0L) {
        setQuantity(other.getQuantity());
      }
      if (other.getTimeinforce() != 0L) {
        setTimeinforce(other.getTimeinforce());
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
      com.tangem.blockchain.binance.proto.NewOrder parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.tangem.blockchain.binance.proto.NewOrder) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private com.google.protobuf.ByteString sender_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <pre>
     *    0xCE6DC043 // hardcoded, object type prefix in 4 bytes
     * </pre>
     *
     * <code>bytes sender = 1;</code>
     */
    public com.google.protobuf.ByteString getSender() {
      return sender_;
    }
    /**
     * <pre>
     *    0xCE6DC043 // hardcoded, object type prefix in 4 bytes
     * </pre>
     *
     * <code>bytes sender = 1;</code>
     */
    public Builder setSender(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }

      sender_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     *    0xCE6DC043 // hardcoded, object type prefix in 4 bytes
     * </pre>
     *
     * <code>bytes sender = 1;</code>
     */
    public Builder clearSender() {

      sender_ = getDefaultInstance().getSender();
      onChanged();
      return this;
    }

    private Object id_ = "";
    /**
     * <pre>
     * order id, optional
     * </pre>
     *
     * <code>string id = 2;</code>
     */
    public String getId() {
      Object ref = id_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        id_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <pre>
     * order id, optional
     * </pre>
     *
     * <code>string id = 2;</code>
     */
    public com.google.protobuf.ByteString
        getIdBytes() {
      Object ref = id_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        id_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * order id, optional
     * </pre>
     *
     * <code>string id = 2;</code>
     */
    public Builder setId(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }

      id_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * order id, optional
     * </pre>
     *
     * <code>string id = 2;</code>
     */
    public Builder clearId() {

      id_ = getDefaultInstance().getId();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * order id, optional
     * </pre>
     *
     * <code>string id = 2;</code>
     */
    public Builder setIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

      id_ = value;
      onChanged();
      return this;
    }

    private Object symbol_ = "";
    /**
     * <pre>
     * symbol for trading pair in full name of the tokens
     * </pre>
     *
     * <code>string symbol = 3;</code>
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
     * symbol for trading pair in full name of the tokens
     * </pre>
     *
     * <code>string symbol = 3;</code>
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
     * symbol for trading pair in full name of the tokens
     * </pre>
     *
     * <code>string symbol = 3;</code>
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
     * symbol for trading pair in full name of the tokens
     * </pre>
     *
     * <code>string symbol = 3;</code>
     */
    public Builder clearSymbol() {

      symbol_ = getDefaultInstance().getSymbol();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * symbol for trading pair in full name of the tokens
     * </pre>
     *
     * <code>string symbol = 3;</code>
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

    private long ordertype_ ;
    /**
     * <pre>
     * only accept 2 for now, meaning limit order
     * </pre>
     *
     * <code>int64 ordertype = 4;</code>
     */
    public long getOrdertype() {
      return ordertype_;
    }
    /**
     * <pre>
     * only accept 2 for now, meaning limit order
     * </pre>
     *
     * <code>int64 ordertype = 4;</code>
     */
    public Builder setOrdertype(long value) {

      ordertype_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * only accept 2 for now, meaning limit order
     * </pre>
     *
     * <code>int64 ordertype = 4;</code>
     */
    public Builder clearOrdertype() {

      ordertype_ = 0L;
      onChanged();
      return this;
    }

    private long side_ ;
    /**
     * <pre>
     * 1 for buy and 2 fory sell
     * </pre>
     *
     * <code>int64 side = 5;</code>
     */
    public long getSide() {
      return side_;
    }
    /**
     * <pre>
     * 1 for buy and 2 fory sell
     * </pre>
     *
     * <code>int64 side = 5;</code>
     */
    public Builder setSide(long value) {

      side_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 1 for buy and 2 fory sell
     * </pre>
     *
     * <code>int64 side = 5;</code>
     */
    public Builder clearSide() {

      side_ = 0L;
      onChanged();
      return this;
    }

    private long price_ ;
    /**
     * <pre>
     * price of the order, which is the real price multiplied by 1e8 (10^8) and rounded to integer
     * </pre>
     *
     * <code>int64 price = 6;</code>
     */
    public long getPrice() {
      return price_;
    }
    /**
     * <pre>
     * price of the order, which is the real price multiplied by 1e8 (10^8) and rounded to integer
     * </pre>
     *
     * <code>int64 price = 6;</code>
     */
    public Builder setPrice(long value) {

      price_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * price of the order, which is the real price multiplied by 1e8 (10^8) and rounded to integer
     * </pre>
     *
     * <code>int64 price = 6;</code>
     */
    public Builder clearPrice() {

      price_ = 0L;
      onChanged();
      return this;
    }

    private long quantity_ ;
    /**
     * <pre>
     * quantity of the order, which is the real price multiplied by 1e8 (10^8) and rounded to integer
     * </pre>
     *
     * <code>int64 quantity = 7;</code>
     */
    public long getQuantity() {
      return quantity_;
    }
    /**
     * <pre>
     * quantity of the order, which is the real price multiplied by 1e8 (10^8) and rounded to integer
     * </pre>
     *
     * <code>int64 quantity = 7;</code>
     */
    public Builder setQuantity(long value) {

      quantity_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * quantity of the order, which is the real price multiplied by 1e8 (10^8) and rounded to integer
     * </pre>
     *
     * <code>int64 quantity = 7;</code>
     */
    public Builder clearQuantity() {

      quantity_ = 0L;
      onChanged();
      return this;
    }

    private long timeinforce_ ;
    /**
     * <pre>
     * 1 for Good Till Expire(GTE) order and 3 for Immediate Or Cancel (IOC)
     * </pre>
     *
     * <code>int64 timeinforce = 8;</code>
     */
    public long getTimeinforce() {
      return timeinforce_;
    }
    /**
     * <pre>
     * 1 for Good Till Expire(GTE) order and 3 for Immediate Or Cancel (IOC)
     * </pre>
     *
     * <code>int64 timeinforce = 8;</code>
     */
    public Builder setTimeinforce(long value) {

      timeinforce_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * 1 for Good Till Expire(GTE) order and 3 for Immediate Or Cancel (IOC)
     * </pre>
     *
     * <code>int64 timeinforce = 8;</code>
     */
    public Builder clearTimeinforce() {

      timeinforce_ = 0L;
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


    // @@protoc_insertion_point(builder_scope:transaction.NewOrder)
  }

  // @@protoc_insertion_point(class_scope:transaction.NewOrder)
  private static final com.tangem.blockchain.binance.proto.NewOrder DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.tangem.blockchain.binance.proto.NewOrder();
  }

  public static com.tangem.blockchain.binance.proto.NewOrder getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<NewOrder>
      PARSER = new com.google.protobuf.AbstractParser<NewOrder>() {
    @Override
    public NewOrder parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new NewOrder(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<NewOrder> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<NewOrder> getParserForType() {
    return PARSER;
  }

  @Override
  public com.tangem.blockchain.binance.proto.NewOrder getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

