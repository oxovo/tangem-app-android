// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: dex.proto

package com.tangem.blockchain.binance.proto;

public final class Transaction {
  private Transaction() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_StdTx_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_StdTx_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_StdSignature_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_StdSignature_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_StdSignature_PubKey_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_StdSignature_PubKey_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_NewOrder_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_NewOrder_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_CancelOrder_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_CancelOrder_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_TokenFreeze_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_TokenFreeze_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_TokenUnfreeze_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_TokenUnfreeze_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_Send_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_Send_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_Send_Token_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_Send_Token_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_Send_Input_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_Send_Input_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_transaction_Send_Output_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_transaction_Send_Output_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\tdex.proto\022\013transaction\"U\n\005StdTx\022\014\n\004msg" +
      "s\030\001 \003(\014\022\022\n\nsignatures\030\002 \003(\014\022\014\n\004memo\030\003 \001(" +
      "\t\022\016\n\006source\030\004 \001(\003\022\014\n\004data\030\005 \001(\014\"f\n\014StdSi" +
      "gnature\022\017\n\007pub_key\030\001 \001(\014\022\021\n\tsignature\030\002 " +
      "\001(\014\022\026\n\016account_number\030\003 \001(\003\022\020\n\010sequence\030" +
      "\004 \001(\003\032\010\n\006PubKey\"\215\001\n\010NewOrder\022\016\n\006sender\030\001" +
      " \001(\014\022\n\n\002id\030\002 \001(\t\022\016\n\006symbol\030\003 \001(\t\022\021\n\torde" +
      "rtype\030\004 \001(\003\022\014\n\004side\030\005 \001(\003\022\r\n\005price\030\006 \001(\003" +
      "\022\020\n\010quantity\030\007 \001(\003\022\023\n\013timeinforce\030\010 \001(\003\"" +
      "<\n\013CancelOrder\022\016\n\006sender\030\001 \001(\014\022\016\n\006symbol" +
      "\030\002 \001(\t\022\r\n\005refid\030\003 \001(\t\";\n\013TokenFreeze\022\014\n\004" +
      "from\030\001 \001(\014\022\016\n\006symbol\030\002 \001(\t\022\016\n\006amount\030\003 \001" +
      "(\003\"=\n\rTokenUnfreeze\022\014\n\004from\030\001 \001(\014\022\016\n\006sym" +
      "bol\030\002 \001(\t\022\016\n\006amount\030\003 \001(\003\"\207\002\n\004Send\022\'\n\006in" +
      "puts\030\001 \003(\0132\027.transaction.Send.Input\022)\n\007o" +
      "utputs\030\002 \003(\0132\030.transaction.Send.Output\032&" +
      "\n\005Token\022\r\n\005denom\030\001 \001(\t\022\016\n\006amount\030\002 \001(\003\032@" +
      "\n\005Input\022\017\n\007address\030\001 \001(\014\022&\n\005coins\030\002 \003(\0132" +
      "\027.transaction.Send.Token\032A\n\006Output\022\017\n\007ad" +
      "dress\030\001 \001(\014\022&\n\005coins\030\002 \003(\0132\027.transaction" +
      ".Send.TokenB*\n\031com.tangem.wallet.binance.proto" +
      "B\013TransactionP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_transaction_StdTx_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_transaction_StdTx_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_StdTx_descriptor,
        new String[] { "Msgs", "Signatures", "Memo", "Source", "Data", });
    internal_static_transaction_StdSignature_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_transaction_StdSignature_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_StdSignature_descriptor,
        new String[] { "PubKey", "Signature", "AccountNumber", "Sequence", });
    internal_static_transaction_StdSignature_PubKey_descriptor =
      internal_static_transaction_StdSignature_descriptor.getNestedTypes().get(0);
    internal_static_transaction_StdSignature_PubKey_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_StdSignature_PubKey_descriptor,
        new String[] { });
    internal_static_transaction_NewOrder_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_transaction_NewOrder_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_NewOrder_descriptor,
        new String[] { "Sender", "Id", "Symbol", "Ordertype", "Side", "Price", "Quantity", "Timeinforce", });
    internal_static_transaction_CancelOrder_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_transaction_CancelOrder_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_CancelOrder_descriptor,
        new String[] { "Sender", "Symbol", "Refid", });
    internal_static_transaction_TokenFreeze_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_transaction_TokenFreeze_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_TokenFreeze_descriptor,
        new String[] { "From", "Symbol", "Amount", });
    internal_static_transaction_TokenUnfreeze_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_transaction_TokenUnfreeze_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_TokenUnfreeze_descriptor,
        new String[] { "From", "Symbol", "Amount", });
    internal_static_transaction_Send_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_transaction_Send_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_Send_descriptor,
        new String[] { "Inputs", "Outputs", });
    internal_static_transaction_Send_Token_descriptor =
      internal_static_transaction_Send_descriptor.getNestedTypes().get(0);
    internal_static_transaction_Send_Token_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_Send_Token_descriptor,
        new String[] { "Denom", "Amount", });
    internal_static_transaction_Send_Input_descriptor =
      internal_static_transaction_Send_descriptor.getNestedTypes().get(1);
    internal_static_transaction_Send_Input_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_Send_Input_descriptor,
        new String[] { "Address", "Coins", });
    internal_static_transaction_Send_Output_descriptor =
      internal_static_transaction_Send_descriptor.getNestedTypes().get(2);
    internal_static_transaction_Send_Output_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_transaction_Send_Output_descriptor,
        new String[] { "Address", "Coins", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
