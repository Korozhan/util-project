package com.korozhan.app.util;

import java.util.Stack;

public class MoneyConverter {
    private static enum Ranges {
        UNITS, DECADES, HUNDREDS, THOUSANDS, MILLIONS, BILLIONS
    }

    public enum Currency {
        BYN, EUR
    }

    private static Stack<ThreeChar> threeChars;

    private static class ThreeChar {
        char h, d, u;
        Ranges range;
    }

    public static String digits2text(String amountString, String currencyString) {
        try {
            return digits2text(new Double(amountString), currencyString);
        } catch (NumberFormatException ex) {
            System.out.println(ex);
            return null;
        }
    }

    public static String digits2text(Double amount, String currencyString) {
        //����� < 1000000
        Currency currency = Currency.valueOf(currencyString);
        String s = amount.toString();
        int n = s.length() - s.lastIndexOf('.');
        if (amount == null || amount < 0.0 || amount > 1000000.0 || n > 3) {
            //���� �������������� ������������� ���� ����� �� 0.0 �� 1000000.0
            return null;
        }
        if (n == 2) {
            s += "0";
        }
        String[] sa = s.split("\\.");
        threeChars = new Stack<>();
        threeChars.push(new ThreeChar());
        threeChars.peek().range = Ranges.UNITS;
        StringBuilder sb = new StringBuilder(sa[0]).reverse();
        for (int i = 0; i < sb.length(); i++) {
            if (i > 0 && i % 3 == 0) {
                threeChars.push(new ThreeChar());
            }
            ThreeChar threeChar = threeChars.peek();
            switch (i) {
                case 0:
                    threeChar.u = sb.charAt(i);
                    break;
                case 3:
                    threeChar.range = Ranges.THOUSANDS;
                    threeChar.u = sb.charAt(i);
                    break;
                case 6:
                    threeChar.range = Ranges.MILLIONS;
                    threeChar.u = sb.charAt(i);
                    break;
                case 9:
                    threeChar.range = Ranges.BILLIONS;
                    threeChar.u = sb.charAt(i);
                    break;
                case 2:
                case 5:
                case 8:
                    threeChar.h = sb.charAt(i);
                    break;
                default:
                    threeChar.d = sb.charAt(i);
            }
        }
        StringBuilder result = new StringBuilder();
        while (!threeChars.isEmpty()) {
            ThreeChar thch = threeChars.pop();
            if (thch.h == '0' && thch.d == '0' && thch.u == '0' && !threeChars.isEmpty()) continue;
            if (thch.h > 0) {
                result.append(getHundreds(thch.h));
                result.append(' ');
            }
            if (thch.d > '0') {
                if (thch.d > '1' || (thch.d == '1' && thch.u == '0')) {
                    result.append(getDecades(thch.d));
                } else if (thch.d > '0') {
                    result.append(getTeens(thch.u));
                }
                result.append(' ');
            }
            if (thch.u > '0' && thch.d != '1') {
                result.append(getUnits(thch.u, thch.range == Ranges.THOUSANDS));
                result.append(' ');
            }
            switch (thch.range) {
                case BILLIONS:
                    if (thch.d == '1' || thch.u == '0') {
                        result.append("����������");
                    } else if (thch.u > '4') {
                        result.append("����������");
                    } else if (thch.u > '1') {
                        result.append("���������");
                    } else {
                        result.append("��������");
                    }
                    break;
                case MILLIONS:
                    if (thch.d == '1' || thch.u == '0') {
                        result.append("���������");
                    } else if (thch.u > '4') {
                        result.append("���������");
                    } else if (thch.u > '1') {
                        result.append("��������");
                    } else {
                        result.append("�������");
                    }
                    break;
                case THOUSANDS:
                    if (thch.d == '1' || thch.u == '0') {
                        result.append("�����");
                    } else if (thch.u > '4') {
                        result.append("�����");
                    } else if (thch.u > '1') {
                        result.append("������");
                    } else {
                        result.append("������");
                    }
                    break;
                default:
                    if (currency == Currency.BYN) {
                        if (thch.d == '1' || thch.u == '0' || thch.u > '4') {
                            result.append("����������� ������");
                        } else if (thch.u > '1') {
                            result.append("����������� �����");
                        } else {
                            result.append("����������� �����");
                        }
                    } else result.append("����");
            }
            result.append(' ');
        }
        result.append(sa[1] + ' ');
        switch (sa[1].charAt(1)) {
            case '1':
                if (currency == Currency.BYN) {
                    result.append(sa[1].charAt(0) != '1' ? "�������" : "������");
                } else {
                    result.append(sa[1].charAt(0) != '1' ? "����" : "������");
                }
                break;
            case '2':
            case '3':
            case '4':
                if (currency == Currency.BYN) {
                    result.append(sa[1].charAt(0) != '1' ? "�������" : "������");
                } else {
                    result.append(sa[1].charAt(0) != '1' ? "�����" : "������");
                }
                break;
            default:
                if (currency == Currency.BYN) {
                    result.append("������");
                } else {
                    result.append("������");
                }
        }
        char first = Character.toUpperCase(result.charAt(0));
        result.setCharAt(0, first);
        return result.toString().replaceAll("null", "").replaceAll("(\\s+)", " ");
    }

    private static String getHundreds(char dig) {
        switch (dig) {
            case '1':
                return "���";
            case '2':
                return "������";
            case '3':
                return "������";
            case '4':
                return "���������";
            case '5':
                return "�������";
            case '6':
                return "�������";
            case '7':
                return "������";
            case '8':
                return "��������";
            case '9':
                return "���������";
            default:
                return null;
        }
    }

    private static String getDecades(char dig) {
        switch (dig) {
            case '1':
                return "������";
            case '2':
                return "��������";
            case '3':
                return "��������";
            case '4':
                return "�����";
            case '5':
                return "���������";
            case '6':
                return "����������";
            case '7':
                return "���������";
            case '8':
                return "�����������";
            case '9':
                return "���������";
            default:
                return null;
        }
    }

    private static String getUnits(char dig, boolean female) {
        switch (dig) {
            case '1':
                return female ? "����" : "����";
            case '2':
                return female ? "���" : "���";
            case '3':
                return "���";
            case '4':
                return "������";
            case '5':
                return "����";
            case '6':
                return "�����";
            case '7':
                return "����";
            case '8':
                return "������";
            case '9':
                return "������";
            default:
                return null;
        }
    }

    private static String getTeens(char dig) {
        String s = "";
        switch (dig) {
            case '1':
                s = "����";
                break;
            case '2':
                s = "���";
                break;
            case '3':
                s = "���";
                break;
            case '4':
                s = "�����";
                break;
            case '5':
                s = "���";
                break;
            case '6':
                s = "����";
                break;
            case '7':
                s = "���";
                break;
            case '8':
                s = "�����";
                break;
            case '9':
                s = "�����";
                break;
        }
        return s + "�������";
    }

    public static void main(String[] args) {
        System.out.println(digits2text("12.34", "BYN"));
        System.out.println(digits2text("12.34", "EUR"));
    }
}
